package com.expleoautomation.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import lombok.NonNull;
import org.apache.commons.lang3.RandomUtils;
import org.assertj.core.api.Assumptions;

/**
 * Utilities to help with collections
 *
 * @author youssef.abdalla
 */
public class CollectionUtils {

  /**
   * @param anyCollection is any non null collection
   * @return random element of the collection or null if the collection is empty Throws {@link
   *     NullPointerException} if the collection is null
   */
  public static <T extends Object> T getRandomElementFromCollection(@NonNull Iterable<T> anyCollection) {
    int targetIndex = getRandomIndexOfCollection(anyCollection);
    if (targetIndex == 0) {
      return null;
    }
    int index = 0;
    Iterator<T> iterator = anyCollection.iterator();
    T result = null;
    while (index < targetIndex) {
      result = iterator.next();
      index++;
    }
    return result;
  }

  /**
   * This will return a random index number based on the length of the collection. This can be used
   * to retrieve a random entry from the collection.
   *
   * @return a random index based on the length of the collection
   */
  public static int getRandomIndexOfCollection(Iterable<?> anyCollection) {
    final String description = "can't choose a random element as the size of the " + "collection is not greater than 0";
    if (anyCollection instanceof Collection) {

	      if (((Collection) anyCollection).isEmpty()) {
	        throw new RuntimeException(description);
	      }
	      return RandomUtils.nextInt(1, ((Collection) anyCollection).size());
     } else {
	      Iterator<?> iterator = anyCollection.iterator();
	      Assumptions.assumeThat(iterator.hasNext()).as(description);
	      int size = 0;
	      while (iterator.hasNext()) {
	        iterator.next();
	        size++;
	      }
	      return RandomUtils.nextInt(1, size);
    }
  }


  /**
   * removes non-alphanumeric charaters from each String in the List of Strings and convert each to
   * lower case
   *
   * @param originalStrList original List of Strings
   * @return List of Strings without any alphanumeric characters and converted to lower case
   */
  public static List<String> removeNonAlphanumericAndConvertToLowerCase(
      @NonNull List<String> originalStrList) {
    List<String> result = new ArrayList<>();
    originalStrList.stream().forEach(orinalStr -> result.add(removeNonAlphanumericAndConvertToLowerCase(orinalStr)));
    return result;
  }

  /**
   * removes non-alphanumeric charaters from String and convert to lower case
   *
   * @param originalStr original string
   * @return originalStr without any alphanumiric characters and converted to lower case
   */
  public static String removeNonAlphanumericAndConvertToLowerCase(@NonNull String originalStr) {
    return originalStr.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
  }
}
