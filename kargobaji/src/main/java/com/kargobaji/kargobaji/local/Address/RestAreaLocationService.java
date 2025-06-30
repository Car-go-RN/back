package com.kargobaji.kargobaji.local.Address;

import com.kargobaji.kargobaji.local.Address.dto.KakaoPlaceResponse;
import com.kargobaji.kargobaji.openAPI.entity.RestArea;
import com.kargobaji.kargobaji.openAPI.repository.RestAreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final RestAreaRepository restAreaRepository;

    @Value("${kakao-rest-api}")
    private String restApi;

    private WebClient webClient;

    private WebClient getWebClient(){
        if(webClient == null){
            webClient = WebClient.builder()
                    .baseUrl("https://dapi.kakao.com")
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + restApi) // kakao rest api 권한 인증하기 위해 header에 전달함.
                    .build();
        }
        return webClient;
    }

    // stdRestNm이 포함된 장소 정보 조회
    public List<KakaoPlaceResponse.Document> getAllPlacesInfo() {
        List<RestArea> restAreaList = restAreaRepository.findAll();

        return restAreaList.stream()
                .map(restArea -> {
                    try {
                        return getPlaceInfo(restArea.getStdRestNm());
                    } catch (NotFoundException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // 조회된 데이터 저장(위도, 경도, 연락처)
    public void updateAllRestAreaLocationInfo(){
        List<RestArea> restAreaList = restAreaRepository.findAll();

        for(RestArea restArea : restAreaList){
            try{
                KakaoPlaceResponse.Document placeInfo = getPlaceInfo(restArea.getStdRestNm());

                // 값이 있을 경우에 업데이트
                restArea.setLatitude(Double.parseDouble(placeInfo.getY()));
                restArea.setLongitude(Double.parseDouble(placeInfo.getX()));
                restArea.setRoadAddress(placeInfo.getRoad_address_name());
                restArea.setPhone(placeInfo.getPhone());
                restArea.setRestAreaNm(placeInfo.getPlace_name());

                restAreaRepository.save(restArea);
            }
            catch(Exception e){
                System.out.println("[" + restArea.getStdRestNm() + "] 업데이트 실패 : " + e.getMessage());;
            }
        }
    }


    // kakao Api 호출
    public KakaoPlaceResponse.Document getPlaceInfo(String stdRestNm) {
        KakaoPlaceResponse.Document result = searchWithCategoryFilter(stdRestNm);

        // 키워드 검색 시 카테고리에 고속도로휴게소/휴게소가 없을 경우
        if(result == null){
            // 휴게소 방향 제거 후 재검색
            String fallback = stdRestNm.replaceAll("\\(.*?\\)", "").trim();
            result = searchWithCategoryFilter(fallback);
        }

        if(result == null){
            throw new NotFoundException("장소를 찾을 수 없습니다. " + stdRestNm);
        }

        return result;
    }

    // 카테고리 필터링
    private KakaoPlaceResponse.Document searchWithCategoryFilter(String keyword){
        KakaoPlaceResponse response = searchKakao(keyword);
        if(response == null || response.getDocuments().isEmpty()) return null;

        return response.getDocuments().stream()
                .filter(document -> {
                    String category = document.getCategory_name();
                    return category != null && (
                            category.contains("고속도로휴게소") || category.contains("휴게소")
                            );
                })
                // 고속도로휴게소 -> 휴게소 정렬 : 고속도로에 있는 휴게소가 아닌 값을 가져올 수 있기 때문에. 우선순위를 둠.
                .min((a, b) -> {
                    boolean aIsHighway = a.getCategory_name().contains("고속도로휴게소");
                    boolean bIsHighway = b.getCategory_name().contains("고속도로휴게소");
                    return Boolean.compare(!aIsHighway, !bIsHighway);
                })
                .orElse(null);
    }

    // kakao map api 검색
    private KakaoPlaceResponse searchKakao(String keyword){
        return getWebClient().get().uri(uriBuilder -> uriBuilder
                    .path("/v2/local/search/keyword.json")
                    .queryParam("query", keyword)
                    .build())
                .retrieve()
                .bodyToMono(KakaoPlaceResponse.class)
                .block();
    }


}
