package com.expleoautomation.commons;

import com.rits.cloning.Cloner;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Keeps track of the {@link TestDataHolder#ENVIRONMENT} values for reporting on the overview page
 * of the Allure report
 *
 * @author youssef.abdalla
 */
public class TestEnvironmentDataHolder {

  private static Set<String> envs = new CopyOnWriteArraySet<>();

  /**
   * @return an exact copy of the test environments set used during the test so far
   */
  public static Set<String> getTestEnvsSet() {
    Cloner cloner = new Cloner();
    return cloner.deepClone(envs);
  }



  /**
   * @see Set#add(Object)
   */
  public static boolean addTestEnvironment(String env) {
    return envs.add(env);
  }

}
