package org.apache.zeppelin.echarts.command;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.zeppelin.echarts.command.reader.Reader;
import org.apache.zeppelin.echarts.command.writer.Writer;
import org.apache.zeppelin.echarts.utils.PropertyGetter;
import org.apache.zeppelin.interpreter.InterpreterContext;
import org.apache.zeppelin.interpreter.InterpreterResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ethan Xiao on 2017/1/12.
 * 命令执行集合
 */
public class Command {
	private List<Executor> executors = new ArrayList<Executor>();

	public void addExecutor(Executor executor, String[] paras, String body) {
		executor.setParameters(paras);
		executor.setBody(body);
		executors.add(executor);
	}

	public InterpreterResult execute(PropertyGetter propertyGetter, InterpreterContext interpreterContext) {
		if (CollectionUtils.isEmpty(executors)) {
			return new InterpreterResult(InterpreterResult.Code.SUCCESS, InterpreterResult.Type.NULL, "");
		}
		if (!(executors.get(0) instanceof Reader)) {
			return new InterpreterResult(InterpreterResult.Code.ERROR, InterpreterResult.Type.NULL, "The first executor must be a reader");
		}
		if (!(executors.get(executors.size() - 1) instanceof Writer)) {
			return new InterpreterResult(InterpreterResult.Code.ERROR, InterpreterResult.Type.NULL, "The last executor must be a writer");
		}
		try {
			Object output = null;
			for (Executor executor : executors) {
				output = executor.execute(output, propertyGetter, interpreterContext);
			}
			return (InterpreterResult)output;
		} catch (Throwable e) {
			return new InterpreterResult(InterpreterResult.Code.ERROR, InterpreterResult.Type.TEXT, ExceptionUtils.getFullStackTrace(e));
		}
	}
}