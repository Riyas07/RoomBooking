/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ecommerce.ecommerce.repository;

import com.ecommerce.ecommerce.model.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 *
 * @author ELCOT
 */
public interface repositoryEmployee extends MongoRepository<Employee, Integer>{
    
}
