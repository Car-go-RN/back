package com.kargobaji.kargobaji.local.Address;

import com.kargobaji.kargobaji.config.NotFoundException;
import com.kargobaji.kargobaji.local.Address.dto.KakaoPlaceResponse;
import com.kargobaji.kargobaji.openAPI.entity.RestAreaGas;
import com.kargobaji.kargobaji.openAPI.repository.RestAreaGasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RestAreaLocationService {

    private final RestAreaGasRepository restAreaGasRepository;

    @Value("${kakao-rest-api}")
    private String restApi;

    private WebClient webClient;

    private WebClient getWebClient(){
        if(webClient == null){
            webClient = WebClient.builder()
                    .baseUrl("https://dapi.kakao.com")
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + restApi)
                    .build();
        }
        return webClient;
    }

    public List<KakaoPlaceResponse.Document> getAllPlacesInfo(){
        List<RestAreaGas> restAreaGasList = restAreaGasRepository.findAll();

        return restAreaGasList.stream()
                .map(restAreaGas ->{
                    try{
                        return getPlaceInfo(restAreaGas.getStdRestNm());
                    }
                    catch(NotFoundException e){
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    // kakao Api 호출
    public KakaoPlaceResponse.Document getPlaceInfo(String stdRestNm) {
        KakaoPlaceResponse response = getWebClient().get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/local/search/keyword.json")
                        .queryParam("query", stdRestNm)
                        .build())
                .retrieve()
                .bodyToMono(KakaoPlaceResponse.class)
                .block();

        if (response == null || response.getDocuments().isEmpty()) {
            throw new NotFoundException("장소를 찾을 수 없습니다 :" + stdRestNm);
        }
        return response.getDocuments().stream()
                .filter(document -> {
                    String category = document.getCategory_name();
                    return category != null &&
                            (category.contains("고속도로휴게소") || category.contains("휴게소"));
                }).min((a, b) -> {
                    boolean aIsHighway = a.getCategory_name().contains("고속도로휴게소");
                    boolean bIsHighway = b.getCategory_name().contains("고속도로휴게소");
                    return Boolean.compare(!aIsHighway, !bIsHighway);
                })
                .orElseThrow(() -> new NotFoundException("고속도로휴게소 또는 휴게소 정보를 찾을 수 없습니다.: " + stdRestNm));
    }
}
