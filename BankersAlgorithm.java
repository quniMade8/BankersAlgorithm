import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class runThisClass {
    //needMatirx = maxMatrix - allocationMatirx
    //如果存在安全序列，则为安全状态；否则为不安全状态。
    public static void main(String[] args) {
                Struct struct = new Struct(3, 5);
                struct.getAvailableResource().put(0, 3);
                struct.getAvailableResource().put(1, 3);
                struct.getAvailableResource().put(2, 2);
                struct.setMaxRequestMatrix(new int[][]{{7, 5, 3}, {3, 2, 2,}, {9, 0, 2}, {2, 2, 2}, {4, 3, 3}});
                struct.setAllocationMatrix(new int[][]{{0, 1, 0}, {2, 0, 0}, {3, 0, 2}, {2, 1, 1}, {0, 0, 2}});
                struct.setNeedMatirx(new int[struct.getProcessNum()][struct.getResourceNum()]);
                //计算needMatrix
                for (int i = 0; i < struct.getProcessNum(); i++) {
                    for (int j = 0; j < struct.getResourceNum(); j++) {
                struct.getNeedMatirx()[i][j] = struct.getMaxRequestMatrix()[i][j] - struct.getAllocationMatrix()[i][j];
            }
        }
        //打印当前状态
        printCurrentStatus(struct);

        //开始一个请求
        System.out.println("输入请求的进程:");
        Scanner sc = new Scanner(System.in);
        int[] request = new int[struct.getResourceNum()];
        //请求的进程p
        int p = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < struct.getResourceNum(); i++) {
            System.out.println("输入请求资源" + i + "的数目:");
            int num = Integer.parseInt(sc.nextLine());
            request[i] = num;
        }

        //判断请求的合法性
        for (int i = 0; i < struct.getResourceNum(); i++) {
            if (request[i] > struct.getNeedMatirx()[p][i]) {
                System.out.println("ERROR:请求的资源超出需求！");
                return;
            }
        }
        for (int i = 0; i < struct.getResourceNum(); i++) {
            if (request[i] > struct.getAvailableResource().get(i)) {
                System.out.println("ERROR:请求的资源超出可分配资源！");
                return;
            }
        }
        //试分配
        for (int i = 0; i < struct.getResourceNum(); i++) {
            int newAR = struct.getAvailableResource().get(i) - request[i];
            struct.getAvailableResource().put(i, newAR);
            struct.getAllocationMatrix()[p][i] += request[i];
            struct.getNeedMatirx()[p][i] -= request[i];
        }
        System.out.println("试分配后的状态:");
        printCurrentStatus(struct);

        //安全性检查
        checkSecurity(struct);
    }

    private static void checkSecurity(Struct struct) {
        //可提供给进程的各资源数目
        HashMap<Integer, Integer> work = (HashMap<Integer, Integer>) struct.getAvailableResource().clone();
        //当有足够资源分配给进程时，finish=true
        HashMap<Integer, Boolean> finish = new HashMap<>();
        for (int i = 0; i < struct.getProcessNum(); i++) {
            finish.put(i, false);
        }

        //从进程集合中找到一个满足以下条件的进程:
        //    finish = false
        //    need <= work
        ArrayList<Integer> saveSequence = new ArrayList<>();//安全序列
        for (int i = 0; i < struct.getProcessNum(); i++) {
            int count = 0;
            for (int j = 0; j < struct.getResourceNum(); j++) {
                if (finish.get(i).equals(false) && struct.getNeedMatirx()[i][j] <= work.get(j)) {
                    count++;
                }
            }
            if (count == struct.getResourceNum()) {
                //找到了满足条件的资源,加入队列
                for (int j = 0; j < struct.getResourceNum(); j++) {
                    int newAM = work.get(j) + struct.getAllocationMatrix()[i][j];
                    work.put(j, newAM);
                }
                finish.put(i, true);
                saveSequence.add(i);
                i = -1;
            }
        }

        for (boolean v : finish.values()) {
            if (!v) {
                System.out.println("ERROR:无法找到安全序列，系统处于不安全状态!");
                return;
            }
        }

        System.out.println("分配资源后线程安全，安全序列:");
        for (int x : saveSequence) {
            System.out.print("进程" + x + " -> ");
        }
        System.out.println("end");
    }

    private static void printCurrentStatus(Struct struct) {
        System.out.println("当前状态:");
        System.out.println("可获得资源:");
        System.out.print("    ");
        for (int i = 0; i < struct.getResourceNum(); i++) {
            System.out.print("资源" + i + "\t");
        }
        System.out.println();
        System.out.print("    ");

        for (int v : struct.getAvailableResource().values()) {
            System.out.print(v + "\t\t");
        }
        System.out.println();
        System.out.println();
        printMatrix("最大需求矩阵", struct);
        printMatrix("分配矩阵", struct);
        printMatrix("需求矩阵", struct);
    }

    private static void printMatrix(String matrixName, Struct struct) {
        System.out.println(matrixName + ":");
        System.out.print("    ");
        for (int i = 0; i < struct.getProcessNum(); i++) {
            System.out.print("进程" + i + "\t");
        }
        System.out.println();
        for (int i = 0; i < struct.getResourceNum(); i++) {
            System.out.print("资源" + i + "\t");
            for (int j = 0; j < struct.getProcessNum(); j++) {
                if (matrixName.equals("最大需求矩阵")) {
                    System.out.print(struct.getMaxRequestMatrix()[j][i] + "\t\t");
                } else if (matrixName.equals("分配矩阵")) {
                    System.out.print(struct.getAllocationMatrix()[j][i] + "\t\t");
                } else if (matrixName.equals("需求矩阵")) {
                    System.out.print(struct.getNeedMatirx()[j][i] + "\t\t");
                }
            }
            System.out.println();
        }
        System.out.println();
    }
}

class Struct {
    public Struct(int resourceNum, int processNum) {
        this.resourceNum = resourceNum;
        this.processNum = processNum;
        this.availableResource = new HashMap<>();
    }

    private int resourceNum;
    private int processNum;
    //可用资源向量,key表示资源号,value表示该资源的可用数目
    private HashMap<Integer, Integer> availableResource;
    //最大需求矩阵,n:进程号,m:资源号,值为最大需求
    private int[][] maxRequestMatrix;
    //分配矩阵,n:进程号,m:资源号,值为已分配数
    private int[][] allocationMatrix;
    //需求矩阵，n:进程号,m:资源号,值为还需资源数
    private int[][] needMatirx;

    public int getResourceNum() {
        return resourceNum;
    }

    public void setResourceNum(int resourceNum) {
        this.resourceNum = resourceNum;
    }

    public int getProcessNum() {
        return processNum;
    }

    public void setProcessNum(int processNum) {
        this.processNum = processNum;
    }

    public HashMap<Integer, Integer> getAvailableResource() {
        return availableResource;
    }

    public void setAvailableResource(HashMap<Integer, Integer> availableResource) {
        this.availableResource = availableResource;
    }

    public int[][] getMaxRequestMatrix() {
        return maxRequestMatrix;
    }

    public void setMaxRequestMatrix(int[][] maxRequestMatrix) {
        this.maxRequestMatrix = maxRequestMatrix;
    }

    public int[][] getAllocationMatrix() {
        return allocationMatrix;
    }

    public void setAllocationMatrix(int[][] allocationMatrix) {
        this.allocationMatrix = allocationMatrix;
    }

    public int[][] getNeedMatirx() {
        return needMatirx;
    }

    public void setNeedMatirx(int[][] needMatirx) {
        this.needMatirx = needMatirx;
    }
}
