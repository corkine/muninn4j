package com.mazhangjing.muninn.v2.service;

import com.mazhangjing.muninn.v2.dao.RewardDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import com.mazhangjing.muninn.v2.reward.*;

@Service
public class RewardService {

    @Autowired RewardDao dao;

    public Map<Reward, Long> getReward() {
        try {
            return dao.getReward();
        } catch (ParseException e) {
            e.printStackTrace();
        } return new HashMap<>();
    }
}
