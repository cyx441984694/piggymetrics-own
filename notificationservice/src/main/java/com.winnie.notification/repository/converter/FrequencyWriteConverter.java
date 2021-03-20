package com.winnie.notification.repository.converter;


import com.winnie.notification.domain.Frequency;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class FrequencyWriteConverter implements Converter<Frequency, Integer> {

    @Override
    public Integer convert(Frequency frequency){return frequency.getDays();}
}
