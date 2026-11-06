package team05.cafe.global.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import team05.cafe.global.error.BusinessException;
import team05.cafe.global.error.ErrorCode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TextNormalizerTest {

    @Test
    @DisplayName("양끝의 공백·탭·줄바꿈만 제거하고 중간 공백은 유지한다")
    void trimEdges_keepsInnerSpaces() {
        assertThat(TextNormalizer.trimEdges(" \t\r\n서울시  실습로 10\n\t ")).isEqualTo("서울시  실습로 10");
    }

    @Test
    @DisplayName("길이는 공백을 제거한 뒤에 검사한다: 앞뒤 공백 포함 32자라도 제거 후 30자면 통과")
    void trimAndCheckLength_checksAfterTrim() {
        String thirty = "a".repeat(30);
        assertThat(TextNormalizer.trimAndCheckLength(" " + thirty + " ", "name", 1, 30, "이유"))
                .isEqualTo(thirty);
    }

    @Test
    @DisplayName("제거 후 비어 있거나 최대 길이를 넘으면 400 VALIDATION_ERROR와 필드·이유를 담는다")
    void trimAndCheckLength_rejectsOutOfRange() {
        assertThatThrownBy(() -> TextNormalizer.trimAndCheckLength(" \t\n ", "name", 1, 30, "1~30자"))
                .isInstanceOfSatisfying(BusinessException.class, e -> {
                    assertThat(e.getErrorCode()).isEqualTo(ErrorCode.INVALID_INPUT);
                    assertThat(e.getField()).isEqualTo("name");
                    assertThat(e.getReason()).isEqualTo("1~30자");
                });

        assertThatThrownBy(() -> TextNormalizer.trimAndCheckLength("a".repeat(31), "name", 1, 30, "1~30자"))
                .isInstanceOf(BusinessException.class);
    }
}
