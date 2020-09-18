package com.shicc.customercenter.service;

import com.shicc.customercenter.domain.Customer;
import com.shicc.customercenter.dto.CustomerDTO;
import com.shicc.customercenter.repository.CustomerRepository;
import com.shicc.customercenter.service.mapper.CustomerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomerService {
    private  final Logger log = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerMapper customerMapper;


    public CustomerDTO getCustomerByRealName(String realName) {
        return customerMapper.toDto(customerRepository.findTopByRealName(realName));
    }
}
