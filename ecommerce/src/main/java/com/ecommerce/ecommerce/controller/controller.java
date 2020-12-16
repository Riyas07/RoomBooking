/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ecommerce.ecommerce.controller;

import com.ecommerce.ecommerce.model.Book;
import com.ecommerce.ecommerce.model.Customer;
import com.ecommerce.ecommerce.model.LoginUser;
import com.ecommerce.ecommerce.repository.repositiryLogin;
import com.ecommerce.ecommerce.repository.repository;
import com.ecommerce.ecommerce.repository.repositoryBooking;
import com.ecommerce.ecommerce.repository.repositoryEmployee;
import com.ecommerce.ecommerce.service.serv;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
/**
 *
 * @author ELCOT
 */
//@CrossOrigin(origins = "http://localhost:3000")
@Controller
public class controller {    
    @Autowired
    serv service;
    @Autowired
    repositoryBooking rb;
    @Autowired
    repository repo;
    @Autowired
    repositiryLogin rl;
    PasswordEncoder passwordEncoder;
@Autowired
    public controller(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
      @Autowired
    repositoryEmployee repoe;
    @RequestMapping("/")
    public String home()
    {
        
        return "home";
    }
    @RequestMapping("/hotel")
    public String hotel()
    {
        return "addhotel";
    }
    @PostMapping("/addhotels")
    public String add(@RequestParam("id") int id,@RequestParam("name")String name,@RequestParam("state") String state,@RequestParam("city")String city,@RequestParam("phonenumber")String phoneNumber,@RequestParam("image")MultipartFile image)
    {
        Customer us=new Customer();
        us.setId(id);
        us.setName(name);
        us.setState(state);
        us.setCity(city);
        us.setPhoneNumber(phoneNumber);
        try
        {
            us.setImage(Base64.getEncoder().encodeToString(image.getBytes())); 
        }
       catch(IOException e)
       {
           System.out.println("image Exception --------------------- "+e);
       }
        repo.insert(us);
return "home";
    }
    @GetMapping("/display")
    public String view(Model model)
    {
        List<Customer>us=repo.findAll();
        //System.out.println(us+"  ........................  ....................................");
        model.addAttribute("us", us);
        return "view";
    }
    @GetMapping("/search")
    public String search(@RequestParam("search")String city,Model model)
    {
        System.out.println(city+" ,,,,,,,,,,,,,,,,,,,,,,,, ");
       List<Customer>searchs= repo.findByCity(city);
       //repo.findByCity(city);
        System.out.println(searchs+" .....................e... ");
        model.addAttribute("city", searchs);
        return "search";
    }
    @RequestMapping("/login")
    public String login()
    {
        return "login";
    }
    @RequestMapping("/logout")
    public String logout()
    {
       return "home";
    }
    @RequestMapping("/createAccount")
    public String createAccount()
    {
        return "createLogin";
    }
    @RequestMapping("/AddAccount")
    public String addAccount(@RequestParam("id") int id,@RequestParam("username") String username,@RequestParam("password") String password)
    {
        LoginUser lu=new LoginUser();
        UserDetails ud=   User.builder()
                .username(username)
                .password(password)
                .roles("ADMIN")
                .build();
        System.out.println("......... "+ud.getPassword());
        lu.setId(id);
        lu.setUsername(ud.getUsername());
        lu.setPassword(passwordEncoder.encode(ud.getPassword()));
        rl.insert(lu);
        return "login";
    }
    @RequestMapping("/book")
    public String book()
    {
        return "book";
    }
    public static final String SUCCESS_URL = "booked/success";
	public static final String CANCEL_URL = "booked/cancel";
    @PostMapping("/booked")
    public String booked(@RequestParam("price")double price,@RequestParam("currency") String currency,@RequestParam("method")String method,@RequestParam("intent")String intent,@RequestParam("description")String description) throws PayPalRESTException
    {
        Book book=new Book();
        book.setTotal(price);
        book.setCurrency(currency);
        book.setMethod(method);
        book.setIntent(intent);
        book.setDescription(description);
        rb.insert(book);
        System.out.println(".................................. price "+book.getTotal());
        System.out.println(".................................. currency "+book.getCurrency());
        Payment payment=  service.createPayment(book.getTotal(), book.getCurrency(),book.getMethod(),book.getIntent(), book.getDescription(), "http://localhost:8030"+CANCEL_URL,  "http://localhost:8030"+SUCCESS_URL);
       for(Links link:payment.getLinks()) {
				if(link.getRel().equals("approval_url")) {
					return "redirect:"+link.getHref();
				}
       
    }
  return "redirect:/book";
}
      @RequestMapping("/lists")
   @ResponseBody
    public List<Book> list()
    {
        return rb.findAll();
    }
    @GetMapping("/img")
    @ResponseBody
    public List<Customer> img()
    {
        return repo.findAll();
    }
 
    @PostMapping("/employee")
    public LoginUser saveEmp(@RequestBody LoginUser employees)
    {
        System.out.println(employees+"................................");
        return rl.save(employees);
    }
    
    @RequestMapping("/emplist")
    @ResponseBody
    public List<LoginUser> epmlist()
    {
        int count=0;
        count++;
       long l= rl.count();
      List<LoginUser> emp= rl.findAll();
     System.out.println("........"+count);
        System.out.println("======  "+emp);
        return rl.findAll();
    }
    @GetMapping(value = CANCEL_URL)
	    public String cancelPay() {
	        return "cancel";
	    }

	    @GetMapping(value = SUCCESS_URL)
	    public String successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
	        try {
	            Payment payment = service.executePayment(paymentId, payerId);
	            System.out.println(payment.toJSON());
	            if (payment.getState().equals("approved")) {
	                return "success";
	            }
	        } catch (PayPalRESTException e) {
	         System.out.println(e.getMessage());
	        }
	        return "redirect:/";
	    }
}
