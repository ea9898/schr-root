package moscow.ptnl.app.trace;

import java.util.ArrayList;
import java.util.List;

/**
* Узел дерева логов, который умеет себя красиво форматировать
*/
public class LogNode {
    
    private String message;
    private List<LogNode> children;

    LogNode(String message, List<LogNode> children) {
        this.message = message;
        this.children = children;
    }

    public LogNode() {
    }

    public moscow.ptnl.app.trace.LogNode getRightmostDeepestChild() {
        moscow.ptnl.app.trace.LogNode current = this;
        while (!current.children.isEmpty()) {
            current = current.children.get(current.children.size() - 1);
        }
        return current;
    }

    public moscow.ptnl.app.trace.LogNode appendChildren(moscow.ptnl.app.trace.LogNode node) {
        if(children == null) {
            children = new ArrayList<>(1);
            children.add(node);
        }
        children.add(node);
        return this;
    }

    public StringBuilder print() {
        StringBuilder sb = new StringBuilder();
        sb.append(System.lineSeparator());
        print("", true, sb);
        return sb;
    }

    private void print(String prefix, boolean isTail, StringBuilder sb) {
        sb.append(prefix)
                .append(isTail ? "+-- " : "+-- ").append(message)
                .append(System.lineSeparator());

        if (children == null)
            return;

        for (int i = 0; i < children.size() - 1; i++) {
            children.get(i).print(prefix + (isTail ? "    " : "|   "), false, sb);
        }

        if (children.size() > 0) {
            children.get(children.size() - 1).print(prefix + (isTail ? "    " : "|   "), true, sb);
        }
    }
}
