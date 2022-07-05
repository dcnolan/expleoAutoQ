package com.expleoautomation.commons;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * In case we need holder for screenshots
 */

public class ScreenshotsFromSteps
    {
    @Getter @Setter private static byte[]       imageAsByteArray;
    @Getter @Setter private static List<byte[]> imagesAsByteArrays;
    }
