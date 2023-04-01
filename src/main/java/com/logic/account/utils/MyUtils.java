package com.logic.account.utils;

import java.net.URI;
import java.util.UUID;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.*;

public class MyUtils {
    public static URI makeLocation(Object obj) {
        return makeLocation(obj, "");
    }

    public static URI makeLocation(Object obj, String path) {
        Object id;
        try {
            val method = obj.getClass().getMethod("getId");
            method.setAccessible(true);
            id = method.invoke(obj);
        } catch (Exception e) {
            return null;
        }

        val location = ServletUriComponentsBuilder
        .fromCurrentRequestUri()
        .path(path + "/{id}")
        .buildAndExpand(id)
        .toUri();

        return location;
    }

    public static String jsonify(Object obj) {
        return jsonify(obj, Include.NON_NULL);
    }

    public static String jsonify(Object obj, Include include) {
        try {
            val mapper = new ObjectMapper().setSerializationInclusion(include);
            return mapper.writerFor(obj.getClass())
                .writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return "";
        }
    }
}
