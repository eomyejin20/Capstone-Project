package com.example.project.service;

import com.example.project.dto.BookDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AladinService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String ALADIN_API_URL = "https://www.aladin.co.kr/ttb/api/ItemSearch.aspx";
    private static final String TTB_KEY = "ttbheebi16541715001";

    public List<BookDto> searchBooks(String keyword) {
        String requestUrl = ALADIN_API_URL +
                "?TTBKey=" + TTB_KEY +
                "&Query=" + keyword +
                "&QueryType=Title&MaxResults=10&SearchTarget=Book&output=js&Version=20131101";

        ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.GET, null, String.class);

        List<BookDto> books = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode items = root.path("item");

            for (JsonNode item : items) {
                String category = item.path("categoryName").asText(); // 🔹 장르
                int totalPages = item.path("subInfo").path("itemPage").asInt(0); // 🔹 전체 페이지 수 (없으면 0)

                BookDto book = new BookDto(
                        item.path("title").asText(),
                        item.path("author").asText(),
                        item.path("publisher").asText(),
                        item.path("pubDate").asText(),
                        item.path("isbn").asText(),
                        item.path("cover").asText(),
                        item.path("link").asText(),
                        category,
                        totalPages
                );
                books.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return books;
    }
}