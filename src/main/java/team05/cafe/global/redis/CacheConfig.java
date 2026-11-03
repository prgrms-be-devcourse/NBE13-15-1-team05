package team05.cafe.global.redis;

import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager.RedisCacheManagerBuilder;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import team05.cafe.domain.menu.dto.MenuResponse;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Configuration
@EnableCaching   // 이게 없으면 @Cacheable 은 주석이나 마찬가지
public class CacheConfig {

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer(ObjectMapper objectMapper) {
        return builder -> builder
                .withCacheConfiguration(CacheNames.MENUS,
                        jsonCache(builder, objectMapper, listOf(objectMapper, MenuResponse.class)));
//                .withCacheConfiguration(CacheNames.MENU_BY_ID,
//                jsonCache(builder, objectMapper, typeOf(objectMapper, MenuResponse.class)));
    }


    private static RedisCacheConfiguration jsonCache(RedisCacheManagerBuilder builder,
                                                     ObjectMapper objectMapper, JavaType valueType) {
        return builder.cacheDefaults()
                .serializeValuesWith(SerializationPair.fromSerializer(
                        new JacksonJsonRedisSerializer<>(objectMapper, valueType)));
    }


    //리스트
    private static JavaType listOf(ObjectMapper objectMapper, Class<?> elementType) {
        return objectMapper.getTypeFactory().constructCollectionType(List.class, elementType);
    }


    //단건
    private static JavaType typeOf(ObjectMapper objectMapper, Class<?> type) {
        return objectMapper.constructType(type);
    }
}