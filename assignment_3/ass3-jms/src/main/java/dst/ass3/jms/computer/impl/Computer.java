package dst.ass3.jms.computer.impl;

import dst.ass3.jms.computer.IComputer;
import dst.ass3.model.TaskComplexity;

public class Computer implements IComputer {

    private final TaskComplexity complexity;
    private final String cluster;
    private final String name;
    private IComputerListener listener;

    public Computer(String name, String cluster, TaskComplexity complexity) {
        this.name = name;
        this.cluster = cluster;
        this.complexity = complexity;
    }

    @Override
    public void start() {
        // TODO Auto-generated method stub

    }

    @Override
    public void stop() {
        // TODO Auto-generated method stub

    }

    @Override
    public void setComputerListener(IComputerListener listener) {
        this.listener = listener;
    }

}
