package sag.model.maze.generators;

import sag.model.maze.Maze;
import sag.model.maze.MazeGenerator;
import sag.model.maze.Point;
import sag.model.maze.structures.ArrayMaze;
import sag.model.maze.structures.MazeStructure;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.Stack;

import static sag.model.maze.Maze.WallDirection;
import static sag.model.maze.Maze.WallDirection.*;

public class RecursiveBacktracking implements MazeGenerator {
    public Point getNew(Point point, WallDirection direction) {
        int x = point.getX();
        int y = point.getY();
        if (direction == WallDirection.E) {
            x++;
        }
        if (direction == WallDirection.W) {
            x--;
        }
        if (direction == WallDirection.N) {
            y--;
        }
        if (direction == WallDirection.S) {
            y++;
        }
        return new Point(x, y);
    }

    private void carveFrom(MazeStructure structure, Point point) {
        Point curr = point;

        WallDirection directions[] = {N, S, W, E};

        boolean canContinue = true;

        Stack<Point> stack = new Stack<>();

        do {
            //System.out.println("x = " + currx + " y = " + curry);
            Collections.shuffle(Arrays.asList(directions));
            boolean found = false;
            for (int directionIndex = 0; directionIndex < 4; directionIndex++) {
                if (moveValid(structure, directions[directionIndex], curr)) {
                    Point newPoint = getNew(curr, directions[directionIndex]);
                    structure.removeWall(curr, directions[directionIndex]);
                    structure.removeWall(newPoint, opposite(directions[directionIndex]));
                    stack.push(curr);
                    curr = newPoint;
                    found = true;
                    break;
                }
            }
            if (!found) {
                curr = stack.pop();
            }
        } while (!stack.empty());
    }

    private WallDirection opposite(WallDirection direction) {
        switch (direction) {
            case N:
                return S;
            case E:
                return W;
            case S:
                return N;
            case W:
                return E;
            default:
                return X;
        }
    }

    private boolean moveValid(MazeStructure structure, WallDirection direction, Point point) {
        Point newPoint = getNew(point, direction);
        return !((newPoint.getX() < 0) || (newPoint.getX() > structure.getWidth() - 1) || (newPoint.getY() < 0) || (newPoint.getY() > structure.getHeight() - 1)) && structure.notVisited(newPoint);
    }

    @Override
    public Maze generate(int width, int height, Point finish) {
        MazeStructure mazeStructure = new ArrayMaze(width, height, finish);
        carveFrom(mazeStructure, new Point(0, 0));
        addLoops(mazeStructure);
        return mazeStructure;
    }

    private void addLoops(MazeStructure mazeStructure) {
        Random generator = new Random();
        WallDirection directions[] = {N, S, W, E};
        int numberOfFields = (int) (0.4 * mazeStructure.getHeight() * mazeStructure.getWidth());
        for (int i = 0; i < numberOfFields; i++) {
            int x = generator.nextInt(mazeStructure.getWidth());
            int y = generator.nextInt(mazeStructure.getHeight());
            Collections.shuffle(Arrays.asList(directions));
            Point curr = new Point(x, y);
            Point newPoint = getNew(curr, directions[0]);
            if (!((newPoint.getX() < 0) || (newPoint.getX() > mazeStructure.getWidth() - 1) || (newPoint.getY() < 0) || (newPoint.getY() > mazeStructure.getHeight() - 1))) {
                mazeStructure.removeWall(curr, directions[0]);
                mazeStructure.removeWall(newPoint, opposite(directions[0]));
            }
        }
    }
}
