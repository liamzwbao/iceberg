/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.iceberg.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.iceberg.relocated.com.google.common.collect.ImmutableMap;
import org.apache.iceberg.relocated.com.google.common.collect.Maps;
import org.apache.iceberg.relocated.com.google.common.io.BaseEncoding;
import org.junit.jupiter.api.Test;

public class TestJsonUtil {

  @Test
  public void get() throws JsonProcessingException {
    assertThatThrownBy(() -> JsonUtil.get("x", JsonUtil.mapper().readTree("{}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse missing field: x");

    assertThatThrownBy(() -> JsonUtil.get("x", JsonUtil.mapper().readTree("{\"x\": null}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse missing field: x");

    assertThat(JsonUtil.get("x", JsonUtil.mapper().readTree("{\"x\": \"23\"}")).asText())
        .isEqualTo("23");
  }

  @Test
  public void getInt() throws JsonProcessingException {
    assertThatThrownBy(() -> JsonUtil.getInt("x", JsonUtil.mapper().readTree("{}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse missing int: x");

    assertThatThrownBy(() -> JsonUtil.getInt("x", JsonUtil.mapper().readTree("{\"x\": null}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse to an integer value: x: null");

    assertThatThrownBy(() -> JsonUtil.getInt("x", JsonUtil.mapper().readTree("{\"x\": \"23\"}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse to an integer value: x: \"23\"");

    assertThatThrownBy(() -> JsonUtil.getInt("x", JsonUtil.mapper().readTree("{\"x\": 23.0}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse to an integer value: x: 23.0");

    assertThat(JsonUtil.getInt("x", JsonUtil.mapper().readTree("{\"x\": 23}"))).isEqualTo(23);
  }

  @Test
  public void getIntOrNull() throws JsonProcessingException {
    assertThat(JsonUtil.getIntOrNull("x", JsonUtil.mapper().readTree("{}"))).isNull();
    assertThat(JsonUtil.getIntOrNull("x", JsonUtil.mapper().readTree("{\"x\": 23}"))).isEqualTo(23);
    assertThat(JsonUtil.getIntOrNull("x", JsonUtil.mapper().readTree("{\"x\": null}"))).isNull();

    assertThatThrownBy(
            () -> JsonUtil.getIntOrNull("x", JsonUtil.mapper().readTree("{\"x\": \"23\"}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse to an integer value: x: \"23\"");

    assertThatThrownBy(
            () -> JsonUtil.getIntOrNull("x", JsonUtil.mapper().readTree("{\"x\": 23.0}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse to an integer value: x: 23.0");
  }

  @Test
  public void getLong() throws JsonProcessingException {
    assertThatThrownBy(() -> JsonUtil.getLong("x", JsonUtil.mapper().readTree("{}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse missing long: x");

    assertThatThrownBy(() -> JsonUtil.getLong("x", JsonUtil.mapper().readTree("{\"x\": null}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse to a long value: x: null");

    assertThatThrownBy(() -> JsonUtil.getLong("x", JsonUtil.mapper().readTree("{\"x\": \"23\"}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse to a long value: x: \"23\"");

    assertThatThrownBy(() -> JsonUtil.getLong("x", JsonUtil.mapper().readTree("{\"x\": 23.0}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse to a long value: x: 23.0");

    assertThat(JsonUtil.getLong("x", JsonUtil.mapper().readTree("{\"x\": 23}"))).isEqualTo(23);
  }

  @Test
  public void getLongOrNull() throws JsonProcessingException {
    assertThat(JsonUtil.getLongOrNull("x", JsonUtil.mapper().readTree("{}"))).isNull();
    assertThat(JsonUtil.getLongOrNull("x", JsonUtil.mapper().readTree("{\"x\": 23}")))
        .isEqualTo(23);
    assertThat(JsonUtil.getLongOrNull("x", JsonUtil.mapper().readTree("{\"x\": null}"))).isNull();

    assertThatThrownBy(
            () -> JsonUtil.getLongOrNull("x", JsonUtil.mapper().readTree("{\"x\": \"23\"}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse to a long value: x: \"23\"");

    assertThatThrownBy(
            () -> JsonUtil.getLongOrNull("x", JsonUtil.mapper().readTree("{\"x\": 23.0}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse to a long value: x: 23.0");
  }

  @Test
  public void getString() throws JsonProcessingException {
    assertThatThrownBy(() -> JsonUtil.getString("x", JsonUtil.mapper().readTree("{}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse missing string: x");

    assertThatThrownBy(() -> JsonUtil.getString("x", JsonUtil.mapper().readTree("{\"x\": null}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse to a string value: x: null");

    assertThatThrownBy(() -> JsonUtil.getString("x", JsonUtil.mapper().readTree("{\"x\": 23}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse to a string value: x: 23");

    assertThat(JsonUtil.getString("x", JsonUtil.mapper().readTree("{\"x\": \"23\"}")))
        .isEqualTo("23");
  }

  @Test
  public void getStringOrNull() throws JsonProcessingException {
    assertThat(JsonUtil.getStringOrNull("x", JsonUtil.mapper().readTree("{}"))).isNull();
    assertThat(JsonUtil.getStringOrNull("x", JsonUtil.mapper().readTree("{\"x\": \"23\"}")))
        .isEqualTo("23");
    assertThat(JsonUtil.getStringOrNull("x", JsonUtil.mapper().readTree("{\"x\": null}"))).isNull();

    assertThatThrownBy(
            () -> JsonUtil.getStringOrNull("x", JsonUtil.mapper().readTree("{\"x\": 23}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse to a string value: x: 23");
  }

  @Test
  public void getByteBufferOrNull() throws JsonProcessingException {
    assertThat(JsonUtil.getByteBufferOrNull("x", JsonUtil.mapper().readTree("{}"))).isNull();
    assertThat(JsonUtil.getByteBufferOrNull("x", JsonUtil.mapper().readTree("{\"x\": null}")))
        .isNull();

    byte[] bytes = new byte[] {1, 2, 3, 4};
    String base16Str = BaseEncoding.base16().encode(bytes);
    String json = String.format("{\"x\": \"%s\"}", base16Str);
    ByteBuffer byteBuffer = JsonUtil.getByteBufferOrNull("x", JsonUtil.mapper().readTree(json));
    assertThat(byteBuffer.array()).isEqualTo(bytes);

    assertThatThrownBy(
            () -> JsonUtil.getByteBufferOrNull("x", JsonUtil.mapper().readTree("{\"x\": 23}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse byte buffer from non-text value: x: 23");
  }

  @Test
  public void getBool() throws JsonProcessingException {
    assertThatThrownBy(() -> JsonUtil.getBool("x", JsonUtil.mapper().readTree("{}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse missing boolean: x");

    assertThatThrownBy(() -> JsonUtil.getBool("x", JsonUtil.mapper().readTree("{\"x\": null}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse to a boolean value: x: null");

    assertThatThrownBy(() -> JsonUtil.getBool("x", JsonUtil.mapper().readTree("{\"x\": \"23\"}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse to a boolean value: x: \"23\"");

    assertThatThrownBy(() -> JsonUtil.getBool("x", JsonUtil.mapper().readTree("{\"x\": \"true\"}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse to a boolean value: x: \"true\"");

    assertThat(JsonUtil.getBool("x", JsonUtil.mapper().readTree("{\"x\": true}"))).isTrue();
    assertThat(JsonUtil.getBool("x", JsonUtil.mapper().readTree("{\"x\": false}"))).isFalse();
  }

  @Test
  public void getBoolOrNull() throws JsonProcessingException {
    assertThatThrownBy(
            () -> JsonUtil.getBoolOrNull("x", JsonUtil.mapper().readTree("{\"x\": \"23\"}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse to a boolean value: x: \"23\"");

    assertThatThrownBy(
            () -> JsonUtil.getBoolOrNull("x", JsonUtil.mapper().readTree("{\"x\": \"true\"}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse to a boolean value: x: \"true\"");

    assertThat(JsonUtil.getBoolOrNull("x", JsonUtil.mapper().readTree("{}"))).isNull();

    assertThat(JsonUtil.getBoolOrNull("x", JsonUtil.mapper().readTree("{\"x\": null}"))).isNull();

    assertThat(JsonUtil.getBoolOrNull("x", JsonUtil.mapper().readTree("{\"x\": true}"))).isTrue();
    assertThat(JsonUtil.getBoolOrNull("x", JsonUtil.mapper().readTree("{\"x\": false}"))).isFalse();
  }

  @Test
  public void getIntArrayOrNull() throws JsonProcessingException {
    assertThat(JsonUtil.getIntArrayOrNull("items", JsonUtil.mapper().readTree("{}"))).isNull();

    assertThat(JsonUtil.getIntArrayOrNull("items", JsonUtil.mapper().readTree("{\"items\": null}")))
        .isNull();

    assertThatThrownBy(
            () ->
                JsonUtil.getIntArrayOrNull(
                    "items", JsonUtil.mapper().readTree("{\"items\": [13, \"23\"]}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse integer from non-int value in items: \"23\"");

    assertThat(
            JsonUtil.getIntArrayOrNull(
                "items", JsonUtil.mapper().readTree("{\"items\": [23, 45]}")))
        .isEqualTo(new int[] {23, 45});
  }

  @Test
  public void getIntegerList() throws JsonProcessingException {
    assertThatThrownBy(() -> JsonUtil.getIntegerList("items", JsonUtil.mapper().readTree("{}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse missing list: items");

    assertThatThrownBy(
            () -> JsonUtil.getIntegerList("items", JsonUtil.mapper().readTree("{\"items\": null}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse JSON array from non-array value: items: null");

    assertThatThrownBy(
            () ->
                JsonUtil.getIntegerList(
                    "items", JsonUtil.mapper().readTree("{\"items\": [13, \"23\"]}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse integer from non-int value in items: \"23\"");

    List<Integer> items = Arrays.asList(23, 45);
    assertThat(
            JsonUtil.getIntegerList("items", JsonUtil.mapper().readTree("{\"items\": [23, 45]}")))
        .isEqualTo(items);

    String json =
        JsonUtil.generate(
            gen -> {
              gen.writeStartObject();
              JsonUtil.writeIntegerArray("items", items, gen);
              gen.writeEndObject();
            },
            false);
    assertThat(JsonUtil.getIntegerList("items", JsonUtil.mapper().readTree(json))).isEqualTo(items);
  }

  @Test
  public void getIntegerSet() throws JsonProcessingException {
    assertThatThrownBy(() -> JsonUtil.getIntegerSet("items", JsonUtil.mapper().readTree("{}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse missing set: items");

    assertThatThrownBy(
            () -> JsonUtil.getIntegerSet("items", JsonUtil.mapper().readTree("{\"items\": null}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse JSON array from non-array value: items: null");

    assertThatThrownBy(
            () ->
                JsonUtil.getIntegerSet(
                    "items", JsonUtil.mapper().readTree("{\"items\": [13, \"23\"]}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse integer from non-int value in items: \"23\"");

    assertThat(JsonUtil.getIntegerSet("items", JsonUtil.mapper().readTree("{\"items\": [23, 45]}")))
        .containsExactlyElementsOf(Arrays.asList(23, 45));
  }

  @Test
  public void getIntegerSetOrNull() throws JsonProcessingException {
    assertThat(JsonUtil.getIntegerSetOrNull("items", JsonUtil.mapper().readTree("{}"))).isNull();

    assertThat(
            JsonUtil.getIntegerSetOrNull("items", JsonUtil.mapper().readTree("{\"items\": null}")))
        .isNull();

    assertThatThrownBy(
            () ->
                JsonUtil.getIntegerSetOrNull(
                    "items", JsonUtil.mapper().readTree("{\"items\": [13, \"23\"]}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse integer from non-int value in items: \"23\"");

    assertThat(
            JsonUtil.getIntegerSetOrNull(
                "items", JsonUtil.mapper().readTree("{\"items\": [23, 45]}")))
        .containsExactlyElementsOf(Arrays.asList(23, 45));
  }

  @Test
  public void getLongList() throws JsonProcessingException {
    assertThatThrownBy(() -> JsonUtil.getLongList("items", JsonUtil.mapper().readTree("{}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse missing list: items");

    assertThatThrownBy(
            () -> JsonUtil.getLongList("items", JsonUtil.mapper().readTree("{\"items\": null}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse JSON array from non-array value: items: null");

    assertThatThrownBy(
            () ->
                JsonUtil.getLongList(
                    "items", JsonUtil.mapper().readTree("{\"items\": [13, \"23\"]}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse long from non-long value in items: \"23\"");

    List<Long> items = Arrays.asList(23L, 45L);
    assertThat(JsonUtil.getLongList("items", JsonUtil.mapper().readTree("{\"items\": [23, 45]}")))
        .isEqualTo(items);

    String json =
        JsonUtil.generate(
            gen -> {
              gen.writeStartObject();
              JsonUtil.writeLongArray("items", items, gen);
              gen.writeEndObject();
            },
            false);
    assertThat(JsonUtil.getLongList("items", JsonUtil.mapper().readTree(json))).isEqualTo(items);
  }

  @Test
  public void getLongListOrNull() throws JsonProcessingException {
    assertThat(JsonUtil.getLongListOrNull("items", JsonUtil.mapper().readTree("{}"))).isNull();

    assertThat(JsonUtil.getLongListOrNull("items", JsonUtil.mapper().readTree("{\"items\": null}")))
        .isNull();

    assertThatThrownBy(
            () ->
                JsonUtil.getLongListOrNull(
                    "items", JsonUtil.mapper().readTree("{\"items\": [13, \"23\"]}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse long from non-long value in items: \"23\"");

    assertThat(
            JsonUtil.getLongListOrNull(
                "items", JsonUtil.mapper().readTree("{\"items\": [23, 45]}")))
        .containsExactlyElementsOf(Arrays.asList(23L, 45L));
  }

  @Test
  public void getLongSet() throws JsonProcessingException {
    assertThatThrownBy(() -> JsonUtil.getLongSet("items", JsonUtil.mapper().readTree("{}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse missing set: items");

    assertThatThrownBy(
            () -> JsonUtil.getLongSet("items", JsonUtil.mapper().readTree("{\"items\": null}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse JSON array from non-array value: items: null");

    assertThatThrownBy(
            () ->
                JsonUtil.getLongSet(
                    "items", JsonUtil.mapper().readTree("{\"items\": [13, \"23\"]}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse long from non-long value in items: \"23\"");

    assertThat(JsonUtil.getLongSet("items", JsonUtil.mapper().readTree("{\"items\": [23, 45]}")))
        .containsExactlyElementsOf(Arrays.asList(23L, 45L));
  }

  @Test
  public void getLongSetOrNull() throws JsonProcessingException {
    assertThat(JsonUtil.getLongSetOrNull("items", JsonUtil.mapper().readTree("{}"))).isNull();

    assertThat(JsonUtil.getLongSetOrNull("items", JsonUtil.mapper().readTree("{\"items\": null}")))
        .isNull();

    assertThatThrownBy(
            () ->
                JsonUtil.getLongSetOrNull(
                    "items", JsonUtil.mapper().readTree("{\"items\": [13, \"23\"]}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse long from non-long value in items: \"23\"");

    assertThat(
            JsonUtil.getLongSetOrNull("items", JsonUtil.mapper().readTree("{\"items\": [23, 45]}")))
        .containsExactlyElementsOf(Arrays.asList(23L, 45L));
  }

  @Test
  public void getStringList() throws JsonProcessingException {
    assertThatThrownBy(() -> JsonUtil.getStringList("items", JsonUtil.mapper().readTree("{}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse missing list: items");

    assertThatThrownBy(
            () -> JsonUtil.getStringList("items", JsonUtil.mapper().readTree("{\"items\": null}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse JSON array from non-array value: items: null");

    assertThatThrownBy(
            () ->
                JsonUtil.getStringList(
                    "items", JsonUtil.mapper().readTree("{\"items\": [\"23\", 45]}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse string from non-text value in items: 45");

    List<String> items = Arrays.asList("23", "45");
    assertThat(
            JsonUtil.getStringList(
                "items", JsonUtil.mapper().readTree("{\"items\": [\"23\", \"45\"]}")))
        .containsExactlyElementsOf(items);

    String json =
        JsonUtil.generate(
            gen -> {
              gen.writeStartObject();
              JsonUtil.writeStringArray("items", items, gen);
              gen.writeEndObject();
            },
            false);
    assertThat(JsonUtil.getStringList("items", JsonUtil.mapper().readTree(json))).isEqualTo(items);
  }

  @Test
  public void getStringListOrNull() throws JsonProcessingException {
    assertThat(JsonUtil.getStringListOrNull("items", JsonUtil.mapper().readTree("{}"))).isNull();

    assertThat(
            JsonUtil.getStringListOrNull("items", JsonUtil.mapper().readTree("{\"items\": null}")))
        .isNull();

    assertThatThrownBy(
            () ->
                JsonUtil.getStringListOrNull(
                    "items", JsonUtil.mapper().readTree("{\"items\": [\"23\", 45]}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse string from non-text value in items: 45");

    assertThat(
            JsonUtil.getStringListOrNull(
                "items", JsonUtil.mapper().readTree("{\"items\": [\"23\", \"45\"]}")))
        .containsExactlyElementsOf(Arrays.asList("23", "45"));
  }

  @Test
  public void getStringSet() throws JsonProcessingException {
    assertThatThrownBy(() -> JsonUtil.getStringSet("items", JsonUtil.mapper().readTree("{}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse missing set: items");

    assertThatThrownBy(
            () -> JsonUtil.getStringSet("items", JsonUtil.mapper().readTree("{\"items\": null}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse JSON array from non-array value: items: null");

    assertThatThrownBy(
            () ->
                JsonUtil.getStringSet(
                    "items", JsonUtil.mapper().readTree("{\"items\": [\"23\", 45]}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse string from non-text value in items: 45");

    assertThat(
            JsonUtil.getStringSet(
                "items", JsonUtil.mapper().readTree("{\"items\": [\"23\", \"45\"]}")))
        .containsExactlyElementsOf(Arrays.asList("23", "45"));
  }

  @Test
  public void getStringMap() throws JsonProcessingException {
    assertThatThrownBy(() -> JsonUtil.getStringMap("items", JsonUtil.mapper().readTree("{}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse missing map: items");

    assertThatThrownBy(
            () -> JsonUtil.getStringMap("items", JsonUtil.mapper().readTree("{\"items\": null}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse string map from non-object value: items: null");

    assertThatThrownBy(
            () ->
                JsonUtil.getStringMap(
                    "items", JsonUtil.mapper().readTree("{\"items\": {\"a\":\"23\", \"b\":45}}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse to a string value: b: 45");

    Map<String, String> items = ImmutableMap.of("a", "23", "b", "45");
    assertThat(
            JsonUtil.getStringMap(
                "items", JsonUtil.mapper().readTree("{\"items\": {\"a\":\"23\", \"b\":\"45\"}}")))
        .isEqualTo(items);

    String json =
        JsonUtil.generate(
            gen -> {
              gen.writeStartObject();
              JsonUtil.writeStringMap("items", items, gen);
              gen.writeEndObject();
            },
            false);
    assertThat(JsonUtil.getStringMap("items", JsonUtil.mapper().readTree(json))).isEqualTo(items);
  }

  @Test
  public void getStringMapNullableValues() throws JsonProcessingException {
    assertThatThrownBy(
            () -> JsonUtil.getStringMapNullableValues("items", JsonUtil.mapper().readTree("{}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse missing map: items");

    assertThatThrownBy(
            () ->
                JsonUtil.getStringMapNullableValues(
                    "items", JsonUtil.mapper().readTree("{\"items\": null}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse string map from non-object value: items: null");

    assertThatThrownBy(
            () ->
                JsonUtil.getStringMapNullableValues(
                    "items", JsonUtil.mapper().readTree("{\"items\": {\"a\":\"23\", \"b\":45}}")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse to a string value: b: 45");

    Map<String, String> itemsWithNullableValues = Maps.newHashMap();
    itemsWithNullableValues.put("a", null);
    itemsWithNullableValues.put("b", null);
    itemsWithNullableValues.put("c", "23");
    assertThat(
            JsonUtil.getStringMapNullableValues(
                "items",
                JsonUtil.mapper()
                    .readTree("{\"items\": {\"a\": null, \"b\": null, \"c\": \"23\"}}")))
        .isEqualTo(itemsWithNullableValues);

    String json =
        JsonUtil.generate(
            gen -> {
              gen.writeStartObject();
              JsonUtil.writeStringMap("items", itemsWithNullableValues, gen);
              gen.writeEndObject();
            },
            false);

    assertThat(JsonUtil.getStringMapNullableValues("items", JsonUtil.mapper().readTree(json)))
        .isEqualTo(itemsWithNullableValues);
  }

  @Test
  public void testGetStringMapOrNull() throws JsonProcessingException {
    String json = "{\"test\": {\"property\": \"value\"}}";
    Map<String, String> map = JsonUtil.getStringMapOrNull("test", JsonUtil.mapper().readTree(json));
    assertThat(map).isEqualTo(Map.of("property", "value"));

    assertThat(JsonUtil.getStringMapOrNull("missing", JsonUtil.mapper().readTree(json))).isNull();
  }

  @Test
  public void testGetObjectList() throws JsonProcessingException {
    String json = "{\"test\": [{\"id\": 1}, {\"id\": 2}], \"not-a-list\": \"value\"}";
    List<Long> list =
        JsonUtil.getObjectList(
            "test", JsonUtil.mapper().readTree(json), node -> JsonUtil.getLong("id", node));

    assertThat(list).isEqualTo(List.of(1L, 2L));

    assertThatThrownBy(
            () ->
                JsonUtil.getObjectList(
                    "missing",
                    JsonUtil.mapper().readTree(json),
                    node -> JsonUtil.getLong("id", node)))
        .hasMessage("Cannot parse missing list: missing")
        .isInstanceOf(IllegalArgumentException.class);

    assertThatThrownBy(
            () ->
                JsonUtil.getObjectList(
                    "not-a-list",
                    JsonUtil.mapper().readTree(json),
                    node -> JsonUtil.getLong("id", node)))
        .hasMessage("Cannot parse JSON array from non-array value: not-a-list: \"value\"")
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void testGetObjectListOrNull() throws JsonProcessingException {
    String json = "{\"test\": [{\"id\": 1}, {\"id\": 2}], \"not-a-list\": \"value\"}";
    List<Long> list =
        JsonUtil.getObjectListOrNull(
            "test", JsonUtil.mapper().readTree(json), node -> JsonUtil.getLong("id", node));

    assertThat(list).isEqualTo(List.of(1L, 2L));

    List<Long> missingList =
        JsonUtil.getObjectListOrNull(
            "missing", JsonUtil.mapper().readTree(json), node -> JsonUtil.getLong("id", node));

    assertThat(missingList).isNull();

    assertThatThrownBy(
            () ->
                JsonUtil.getObjectList(
                    "not-a-list",
                    JsonUtil.mapper().readTree(json),
                    node -> new AtomicLong(JsonUtil.getLong("id", node))))
        .hasMessage("Cannot parse JSON array from non-array value: not-a-list: \"value\"")
        .isInstanceOf(IllegalArgumentException.class);
  }
}
