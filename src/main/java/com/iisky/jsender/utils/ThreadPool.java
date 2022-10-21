/*
 * Copyright (c) 2021 JSender Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iisky.jsender.utils;

import org.slf4j.MDC;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author iisky1121
 * @date 2021-09-01
 */
public class ThreadPool {

    public static Integer CPU_CORES = Runtime.getRuntime().availableProcessors();

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(CPU_CORES * 2, CPU_CORES * 4,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>());

    public static <T> void execute(List<T> list, Consumer<T> consumer) {
        getMDC(map -> execute(new ArrayList<Runnable>() {{
            for (T t : list) {
                add(() -> {
                    if (map != null) {
                        MDC.setContextMap(map);
                    }
                    consumer.accept(t);
                    MDC.clear();
                });
            }
        }}));
    }

    private static void execute(List<Runnable> list) {
        for (Runnable callable : list) {
            executor.execute(callable);
        }
    }

    private static void getMDC(Consumer<Map<String, String>> consumer) {
        Map<String, String> map = MDC.getCopyOfContextMap();
        consumer.accept(map);
    }
}
