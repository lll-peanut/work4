package com.peanut.im.service.Impl;

import com.peanut.im.eneity.ChatMessage;
import com.peanut.im.service.ConvMetaService;
import org.springframework.stereotype.Service;

/**
 * @author: peanut
 * @date: 2026/3/18
 * @version:1.0
 */
@Service
public class ConvMetaServiceImpl implements ConvMetaService {
    // todo 会话
    @Override
    public void insert(ChatMessage msg) {
        System.out.println("插入会话");
    }
}
