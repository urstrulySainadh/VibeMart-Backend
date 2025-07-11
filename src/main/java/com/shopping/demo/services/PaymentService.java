package com.shopping.demo.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.shopping.demo.entities.CartItem;
import com.shopping.demo.entities.Order;
import com.shopping.demo.entities.OrderItem;
import com.shopping.demo.entities.OrderStatus;
import com.shopping.demo.repositories.CartRepository;
import com.shopping.demo.repositories.OrderItemsRepository;
import com.shopping.demo.repositories.OrderRepository;

import jakarta.transaction.Transactional;

@Service
public class PaymentService {

	 @Value("${razorpay.key_id}")
      private String razorpayKeyId;

    @Value("${razorpay.key_secret}")
      private String razorpayKeySecret;
    
      private final OrderRepository orderRepository;
    
      private final OrderItemsRepository orderItemsRepository;
    
      private final CartRepository cartRepository;

	  public PaymentService(OrderRepository orderRepository, OrderItemsRepository orderItemsRepository, CartRepository cartRepository) {
		  this.orderRepository = orderRepository;
		  this.orderItemsRepository = orderItemsRepository;
		  this.cartRepository = cartRepository;
	}
    
	@Transactional
    public String createOrder(int userId, BigDecimal totalAmount, List<OrderItem> cartItems) throws RazorpayException {
        // STEP-1 GENERATE RAZORPAY ORDER
		// STEP-1A INSTANTIATE RAZORPAY CLIENT USING KEYID AND SECRETKEY
		// CREATE RAZORPAY CLIENT
		
        RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

        
        //STEP-1B CREATE THE ORDER REQUEST WHICH SHOULD CONTAIN AMOUNT, CURRENCY TYPE & RECIEPT
        // PREPARE RAZOR PAY ORDER REQUEST IN JSON FORMAT
        var orderRequest = new JSONObject();
        orderRequest.put("amount", totalAmount.multiply(BigDecimal.valueOf(100)).intValue()); // Amount in paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "txn_" + System.currentTimeMillis());
        
        // STEP-1C CREATE RAZORPAY ORDER USING ORDER REQUEST CREATED IN JSON OBJECT FORMAT
        // Create Razorpay order
        com.razorpay.Order razorpayOrder = razorpayClient.orders.create(orderRequest);

        // STORE THE ORDER IN THE DATABASE
        // Save order details in the database
        Order order = new Order();
        order.setOrderId(razorpayOrder.get("id"));
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        orderRepository.save(order);
        
        // STEP-3 RETURN ORDER ID CREATED FOR MAKING PAYMENT
        return razorpayOrder.get("id");
    }

    @Transactional
    public boolean verifyPayment(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature, int userId) {
        try {
            // Prepare signature validation attributes
            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id", razorpayOrderId);
            attributes.put("razorpay_payment_id", razorpayPaymentId);
            attributes.put("razorpay_signature", razorpaySignature);

            // Verify Razorpay signature
            boolean isSignatureValid = com.razorpay.Utils.verifyPaymentSignature(attributes, razorpayKeySecret);

            if (isSignatureValid) {
                // Update order status to SUCCESS
                Order order = orderRepository.findById(razorpayOrderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));
                order.setStatus(OrderStatus.SUCCESS);
                order.setUpdatedAt(LocalDateTime.now());
                orderRepository.save(order);

                // Fetch cart items for the user
                List<CartItem> cartItems = cartRepository.findCartItemsWithProductDetails(userId);

                // Save order items
                for (CartItem cartItem : cartItems) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setProductId(cartItem.getProduct().getProductId());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setPricePerUnit(cartItem.getProduct().getPrice());
                    orderItem.setTotalPrice(cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
                    orderItemsRepository.save(orderItem);
                }

                // Clear user's cart
                cartRepository.deleteAllCartItemsByUserId(userId);

                return true;
            } 
            else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public void saveOrderItems(String orderId, List<OrderItem> items) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        for (OrderItem item : items) {
            item.setOrder(order);
            orderItemsRepository.save(item);
        }
    }
}
