/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.seatunnel.engine.server.dag.physical;

import org.apache.seatunnel.engine.server.task.TaskGroupInfo;

import java.util.List;

public class PhysicalPlan {

    private final List<SubPlan> plans;

    public PhysicalPlan(List<SubPlan> plans) {
        this.plans = plans;
    }

    public List<SubPlan> getPlans() {
        return plans;
    }

    public static class SubPlan {
        private final List<TaskGroupInfo> tasks;

        private final List<TaskGroupInfo> coordinatorTasks;

        public SubPlan(List<TaskGroupInfo> tasks, List<TaskGroupInfo> coordinatorTasks) {
            this.tasks = tasks;
            this.coordinatorTasks = coordinatorTasks;
        }

        public List<TaskGroupInfo> getTasks() {
            return tasks;
        }

        public List<TaskGroupInfo> getCoordinatorTasks() {
            return coordinatorTasks;
        }
    }
}