/*
 * Copyright (c) 2008-2021, Hazelcast, Inc. All Rights Reserved.
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

package org.apache.seatunnel.engine.common.utils;

import org.apache.seatunnel.engine.common.exception.JobDefineCheckExceptionSeaTunnel;
import org.apache.seatunnel.engine.common.exception.JobNotFoundExceptionSeaTunnel;
import org.apache.seatunnel.engine.common.exception.SeaTunnelEngineException;

import com.hazelcast.client.impl.protocol.ClientExceptionFactory;
import com.hazelcast.client.impl.protocol.ClientProtocolErrorCodes;
import com.hazelcast.instance.impl.OutOfMemoryErrorDispatcher;
import lombok.NonNull;
import org.apache.commons.lang3.tuple.ImmutableTriple;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

public final class ExceptionUtil {

    private static final List<ImmutableTriple<Integer, Class<? extends Throwable>, ClientExceptionFactory.ExceptionFactory>> EXCEPTIONS = Arrays.asList(
        new ImmutableTriple<>(ClientProtocolErrorCodes.USER_EXCEPTIONS_RANGE_START, SeaTunnelEngineException.class, SeaTunnelEngineException::new),
        new ImmutableTriple<>(ClientProtocolErrorCodes.USER_EXCEPTIONS_RANGE_START + 1, JobNotFoundExceptionSeaTunnel.class, JobNotFoundExceptionSeaTunnel::new),
        new ImmutableTriple<>(ClientProtocolErrorCodes.USER_EXCEPTIONS_RANGE_START + 2, JobDefineCheckExceptionSeaTunnel.class, JobDefineCheckExceptionSeaTunnel::new)
    );

    private ExceptionUtil() {
    }

    /**
     * Called during startup to make our exceptions known to Hazelcast serialization
     */
    public static void registerSeaTunnelExceptions(@NonNull ClientExceptionFactory factory) {
        for (ImmutableTriple<Integer, Class<? extends Throwable>, ClientExceptionFactory.ExceptionFactory> exception : EXCEPTIONS) {
            factory.register(exception.left, exception.middle, exception.right);
        }
    }

    @NonNull
    public static RuntimeException rethrow(@NonNull final Throwable t) {
        if (t instanceof Error) {
            if (t instanceof OutOfMemoryError) {
                OutOfMemoryErrorDispatcher.onOutOfMemory((OutOfMemoryError) t);
            }
            throw (Error) t;
        } else {
            throw peeledAndUnchecked(t);
        }
    }

    @NonNull
    private static RuntimeException peeledAndUnchecked(@NonNull Throwable t) {
        t = peel(t);

        if (t instanceof RuntimeException) {
            return (RuntimeException) t;
        }

        return new SeaTunnelEngineException(t);
    }

    /**
     * If {@code t} is either of {@link CompletionException}, {@link ExecutionException}
     * or {@link InvocationTargetException}, returns its cause, peeling it recursively.
     * Otherwise returns {@code t}.
     *
     * @param t Throwable to peel
     * @see #peeledAndUnchecked(Throwable)
     */
    public static Throwable peel(Throwable t) {
        while ((t instanceof CompletionException
            || t instanceof ExecutionException
            || t instanceof InvocationTargetException)
            && t.getCause() != null
            && t.getCause() != t
        ) {
            t = t.getCause();
        }
        return t;
    }
}