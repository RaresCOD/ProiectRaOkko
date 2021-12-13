package ro.ubbcluj.map.proiectraokko4.Conexitate;

import ro.ubbcluj.map.proiectraokko4.domain.Prietenie;
import ro.ubbcluj.map.proiectraokko4.domain.Utilizator;

import java.util.LinkedList;

/**
 * Cumonity operation class
 */
public class DFS {
    private int size, nrComp, nrNod;
    private int[][] matrix;
    private int[] viz, ant;
    private Iterable<Prietenie> prietenii;
    private Iterable<Utilizator> users;
    private int maxi;


    private boolean isUser(int x) {
        for(Utilizator util:users) {
            if (util.getId().equals(Long.valueOf(x))) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param nrNod - number of vertices
     * @param prietenii - friendships
     * @param users - users
     */
    public DFS(int nrNod, Iterable<Prietenie> prietenii, Iterable<Utilizator> users) {

        this.users = users;
        this.size = nrNod;
//        System.out.println(size);
        this.nrNod = nrNod;
//        System.out.println(nrNod);
        this.prietenii = prietenii;
        this.nrComp = 0;
        this.matrix = new int[size][size];
        this.viz = new int[nrNod];
        this.ant = new int[nrNod];
        for(int i=0;i<nrNod;i++)
            viz[i] = 0;
        for(int i=0;i<nrNod;i++)
            ant[i] = -1;
        for (int i=0;i<size;i++) {
            for (int j=0;j<size;j++) {
                matrix[i][j] = 0;
            }
        }

        for (Prietenie curent : prietenii) {
            matrix[Math.toIntExact(curent.getId().getLeft())-1][Math.toIntExact(curent.getId().getRight())-1] = 1;
            matrix[Math.toIntExact(curent.getId().getRight())-1][Math.toIntExact(curent.getId().getLeft())-1] = 1;
        }
    }

    private void run(int x) {
//        System.out.println(x);
        viz[x] = nrComp;
        for(int i=0;i<nrNod;i++) {
            if(matrix[x][i] == 1 && viz[i] == 0) run(i);
        }
    }

    private void run2(int x, int ant) {
        viz[x] = viz[ant] + 1;
        for(int i=0;i<nrNod;i++) {
            if(matrix[x][i] == 1 && viz[i] == 0) run2(i, x);
        }
    }

    private void bfs(int x) {

        viz[x] = 1;
        LinkedList<Integer> queue = new LinkedList<Integer>();
        queue.add(x);
        while (!queue.isEmpty()) {
            x = queue.poll();
            for ( int i=0;i<nrNod;i++) {
                if(viz[i]==0 && matrix[x][i] == 1) {
                    queue.add(i);
                    viz[i] = viz[x] + 1;
                }
            }
        }
    }

    private void CompCon() {
        for(int i=0;i<nrNod;i++) {
            if(viz[i] == 0 && isUser(i+1)) {
                nrComp++;
                run(i);
            }
        }
    }

    private void BT(int x) {
//        viz[x] = 1;
        for(int i=0;i<nrNod;i++) {
            if(matrix[x][i]!=0 && viz[i]==0) {
                viz[i]=viz[x]+1;
                if(maxi < viz[i]) {
                    maxi = viz[i];
                }
                BT(i);
                viz[i]=0;
            }
        }
    }

    private int LongRoad() {
        for(int i=0;i<nrNod;i++)
            viz[i] = 0;
        for(int i=0;i<nrNod;i++) {
            if (isUser(i+1)) {
                bfs(i);
                break;
            }
        }
        int max = 0, poz = 0;
        for(int i=0;i<nrNod;i++)  {
            if(max<viz[i]) {
                max = viz[i];
                poz = i;
            }
            viz[i] = 0;
        }

        BT(poz);
        return maxi;

    }

    /**
     *
     * @return nr Comp conexe
     */
    public int execute1() {
        CompCon();

        return nrComp;
    }

    /**
     *
     * @return drum maxim
     */
    public int execute2() {
        return LongRoad();
    }
}
