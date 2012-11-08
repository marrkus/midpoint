/**
 * Copyright (c) 2011 Evolveum
 *
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://www.opensource.org/licenses/cddl1 or
 * CDDLv1.0.txt file in the source code distribution.
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted 2011 [name of copyright owner]"
 * 
 */
package com.evolveum.midpoint.task.api;

import com.evolveum.midpoint.xml.ns._public.common.common_2a.TaskExecutionStatusType;

/**
 * Task execution status.
 * 
 * Execution status provides information about the task overall high-level
 * execution state. It tells whether the task is running/runnable, waits for
 * something or is done.
 * 
 * @author Radovan Semancik
 * 
 */
public enum TaskExecutionStatus {
	/**
	 * The task is running or is ready to be executed. This state implies that
	 * the task is being actively executed by IDM nodes, e.g. there is a thread
	 * on one of the IDM nodes that executes the task or the system needs to
	 * allocate such thread.
	 */
	RUNNABLE,

	/**
	 * The IDM system is waiting while the task is being executed on an external
	 * node (e.g. external workflow engine) or is waiting for some kind of
	 * external signal (e.g. approval in internal workflow). The task may be
	 * running on external node or blocked on IDM node. One way or another,
	 * there is no point in allocating a thread to run this task. Other task
	 * properties provide more information about the actual "business" state of
	 * the task.
	 */
	WAITING,
	
	/**
	 * The task has been suspended. It waits until an instruction to resume it arrives.
	 * After that, it will (usually) go to the RUNNABLE state again. Or, it can be closed
	 * in the suspended state as well.
	 * 
	 * If a task that is currently executing (i.e. claimed) is suspended, it will first
	 * be in SUSPENDED/CLAIMED state, until its handler finishes the execution. After that,
	 * it will go into SUSPENDED/RELEASED state, where it will remain until resumed. 
	 */
	SUSPENDED,

	/**
	 * The task is done. No other changes or progress will happen. The task in
	 * this state is considered immutable and the only things that can happen to
	 * it is a delete by a cleanup code.
	 */
	CLOSED;

	public static TaskExecutionStatus fromTaskType(TaskExecutionStatusType executionStatus) {
		if (executionStatus == null) {
			return null;
		}
		if (executionStatus == TaskExecutionStatusType.RUNNABLE) {
			return RUNNABLE;
		}
		if (executionStatus == TaskExecutionStatusType.WAITING) {
			return WAITING;
		}
		if (executionStatus == TaskExecutionStatusType.CLOSED) {
			return CLOSED;
		}
		if (executionStatus == TaskExecutionStatusType.SUSPENDED) {
			return SUSPENDED;
		}
		throw new IllegalArgumentException("Unknown execution status type "+executionStatus);
	}

	public TaskExecutionStatusType toTaskType() {
		if (this== RUNNABLE) {
			return TaskExecutionStatusType.RUNNABLE;
		}
		if (this==WAITING) {
			return TaskExecutionStatusType.WAITING;
		}
		if (this==CLOSED) {
			return TaskExecutionStatusType.CLOSED;
		}
		if (this==SUSPENDED) {
			return TaskExecutionStatusType.SUSPENDED;
		}
		throw new IllegalArgumentException("Unknown execution status type "+this);
	}
}
