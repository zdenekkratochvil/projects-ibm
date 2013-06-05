/**
 * 
 */
package com.ibm.rio.log.remote.builders.utils;

import java.util.Stack;

import com.ibm.rio.log.remote.model.AbstractEntity;
import com.ibm.rio.log.remote.model.Transition;
import com.ibm.rio.log.remote.model.enums.TransitionType;

/**
 * @author Zdenek Kratochvil
 *
 */
public final class EntityStackUtil {
	
	private interface ICallback<T extends AbstractEntity> {
		T call(AbstractEntity entity);
	}

	public static Transition findFirst(Stack<AbstractEntity> stack, final TransitionType transitionType) {
		return iterateStack(stack, new ICallback<Transition>() {
			public Transition call(AbstractEntity entity) {
				if(entity instanceof Transition) {
					Transition tr = (Transition) entity;
					if(transitionType.equals(tr.getType())) {
						return tr;
					}
				}
				return null;
			}
		});
	}

	public static Transition findFirstAndRemoveUpToFound(Stack<AbstractEntity> stack, final TransitionType transitionType) {
		return iterateStackAndRemoveUpToFound(stack, new ICallback<Transition>() {
			public Transition call(AbstractEntity entity) {
				if(entity instanceof Transition) {
					Transition tr = (Transition) entity;
					if(transitionType.equals(tr.getType())) {
						return tr;
					}
				}
				return null;
			}
		});
	}
	
	private static <T extends AbstractEntity> T iterateStack(Stack<AbstractEntity> stack, ICallback<T> callback) {
		for(int index = stack.size() - 1; index > -1; index--) {
			T entity = callback.call(stack.get(index));
			if(entity != null) {
				return entity;
			}
		}
		return null;
	}
	
	private static <T extends AbstractEntity> T iterateStackAndRemoveUpToFound(Stack<AbstractEntity> stack, ICallback<T> callback) {
		AbstractEntity entity = peek(stack);
		if(entity == null) {
			return null;
		}
		
		T result = null;
		while((result = callback.call(entity)) == null) {
			stack.pop();
			entity = stack.peek();
		}
		return result;
	}

	public static AbstractEntity peek(Stack<AbstractEntity> stack) {
		if(stack.isEmpty()) {
			return null;
		}
		return stack.peek();
	}
}
