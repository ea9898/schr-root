package moscow.ptnl.app.trace;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/** 
 * Узел дерева логов, который умеет считать время своего выполнения.
 * Created by abelyakov on 12.08.15.
 */
class TreeMethodCall {
    
    private static final String ROOT = "ROOT";
    
    final moscow.ptnl.app.trace.TreeMethodCall parent;
    private final String name;
    private final long startTime;
    private long endTime;
    private List<TreeMethodCall> children = null;
    private String executionId; //идентификатор исполняемого запроса/задачи
    
    private TreeMethodCall(String name, moscow.ptnl.app.trace.TreeMethodCall parent) {
        this.name = name;
        this.startTime = System.currentTimeMillis();
        this.parent = parent;
    }

    /**
     * Конструктор для корневого (ROOT) узла.
     * 
     */
    private TreeMethodCall() {
        this.name = ROOT;
        this.startTime = System.currentTimeMillis();
        this.parent = null;
    }

    static moscow.ptnl.app.trace.TreeMethodCall root() {
        return new moscow.ptnl.app.trace.TreeMethodCall();
    }

    public static moscow.ptnl.app.trace.TreeMethodCall create(String name, moscow.ptnl.app.trace.TreeMethodCall parent) {
        return new moscow.ptnl.app.trace.TreeMethodCall(name, parent);
    }

    void addNestedCall(moscow.ptnl.app.trace.TreeMethodCall call) {
        if (children == null) {
            children = new LinkedList<>();
        }
        children.add(call);
    }

    void stopWatch() {
        endTime = System.currentTimeMillis();
    }

    private long millisElapsed() {
        if (endTime == 0)
            throw new RuntimeException("time measurement is not ended yet!");
        return endTime - startTime;
    }

    private long cleanTime() {
        long millisElapsed = millisElapsed();
        if (children != null) {
            long dirties = children.stream().mapToLong(moscow.ptnl.app.trace.TreeMethodCall::millisElapsed).sum();
            return millisElapsed - dirties;
        } else {
            return millisElapsed;
        }
    }

    @Override
    public String toString() {
        return String.format(
                "%-50s%-20s%s%n",
                name,
                " dirtyTime: " + millisElapsed() + "ms",
                " clean time: " + cleanTime() + "ms");
    }

    /***
     * Конвертировать в древовидное представление, которое умеет себя распечатать
     *
     */
    LogNode toLogNode() {
        List<LogNode> lnChilds = children == null
                ? null
                : children.stream()
                .map(moscow.ptnl.app.trace.TreeMethodCall::toLogNode)
                .collect(Collectors.toList());
        return new LogNode(getLogMessage(), lnChilds);
    }

    private String getLogMessage() {
        StringBuilder sb = new StringBuilder(name);
            if (Objects.equals(name, ROOT)) {
                sb.append(executionId != null ? " [ExchangeId: " + executionId + "]" : "");
            } else {
                sb.append("()");
            }
            sb.append(" ")
              .append(millisElapsed())
              .append("ms");
        return sb.toString();
    }

    /**
     * @return the executionId
     */
    String getExecutionId() {
        return executionId;
    }
    
    void setExecutionId(String executionId) {
        if (Objects.equals(name, ROOT)) {
            this.executionId = executionId;
        } else {
            throw new IllegalStateException("Идентификатор выполнения назначается не корневому узлу");
        }
    }
}
