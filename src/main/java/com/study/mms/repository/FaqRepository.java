package com.study.mms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.mms.dto.CreateFaqDTO;
import com.study.mms.model.Faq;

public interface FaqRepository extends JpaRepository<Faq, Integer> {


}
