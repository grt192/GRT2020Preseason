package frc.pathfinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.fieldmap.geometry.Vector;
import frc.robot.Robot;

public class Pathfinding {

    private HashSet<Node> nodes;
    private Node targetNode;
    private NetworkTableEntry path;

    public Pathfinding() {
        path = NetworkTableInstance.getDefault().getTable("Pathfinding").getEntry("path");
        path.setDoubleArray(new double[0]);
        initNodes();
        targetNode = new Node(new Vector(0, 0));// to avoid null checks
    }

    public Vector search(double x, double y) {
        HashSet<Node> closed = new HashSet<>();
        PriorityQueue<Node> open = new PriorityQueue<>();
        cleanTree();
        Node startNode = new Node(new Vector(x, y));
        addNode(startNode);
        startNode.calcH(targetNode);
        open.add(startNode);

        while (!open.isEmpty()) {
            Node current = open.poll();
            closed.add(current);

            if (current == targetNode) {
                ArrayList<Double> list = new ArrayList<>();
                Node next = current;
                list.add(next.pos.y);
                list.add(next.pos.x);
                while (next.parent != startNode) {
                    next = next.parent;
                    list.add(next.pos.y);
                    list.add(next.pos.x);
                }
                int len = list.size();
                double[] data = new double[len];
                for (int i = 0; i < len; ++i) {
                    data[i] = list.get(len - 1 - i);
                }
                path.setDoubleArray(data);
                removeNode(startNode);
                return next.pos;
            }
            for (Node node : current.neighbors) {
                if (closed.contains(node))
                    continue;
                if (!open.contains(node))
                    open.add(node);
                node.update(current);
            }
        }
        path.setDoubleArray(new double[0]);
        removeNode(startNode);
        return null;
    }

    private void initNodes() {
        nodes = new HashSet<>();
        Set<Vector> pos = Robot.FIELD_MAP.generateNodes();
        System.out.println(pos);
        for (Vector v : pos)
            addNode(new Node(v));
        // etc, etc
    }

    public void setTargetNode(double x, double y) {
        removeNode(targetNode);
        targetNode = new Node(new Vector(x, y));
        addNode(targetNode);
        for (Node node : nodes) {
            node.calcH(targetNode);
        }
    }

    private void cleanTree() {
        for (Node node : nodes) {
            node.g = Double.POSITIVE_INFINITY;
        }
    }

    private void addNode(Node n) {
        for (Node node : nodes) {
            if (Robot.FIELD_MAP.lineOfSight(n.pos, node.pos)) {
                node.neighbors.add(n);
                n.neighbors.add(node);
            }
        }
        nodes.add(n);
    }

    private void removeNode(Node n) {
        nodes.remove(n);
        for (Node node : nodes) {
            node.neighbors.remove(n);
        }
    }

}
