package com.qdw.lpnet;

import com.qdw.lpnet.NetworkTest.Type;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;


public class RoutingAlgorithm {
//	public static enum Type_RA{minC,maxB};
	
	NetworkTest nw;
	public RoutingAlgorithm(NetworkTest nw) {
		// TODO Auto-generated constructor stub
		this.nw = nw;
	}

	public String getPath(int numOfVexs, Type type, int k, Type_RA type_ra) throws IOException {
		return getPath(numOfVexs,type,k,type_ra,0);
	}

	public String getPath(int numOfVexs, Type type, int k, Type_RA type_ra,int time) throws IOException {
		File file = new File("Ĭ��·��.txt");
		if(!file.exists()){
			file.createNewFile();
		}
		InputStream is = new FileInputStream(file);
        Reader reader = new InputStreamReader(is);
        BufferedReader bufferedReader = new BufferedReader(reader);
        
        String line = null;
        boolean ishave = false;
        while ((line = bufferedReader.readLine()) != null) {
            if(line.equals(type.toString()+"#"+k+"#"+type_ra)) {
            	ishave = true;
            	System.out.println("�л���");
            	line = bufferedReader.readLine();
            	break;
            }


        }
		//����ļ���û��
        if(!ishave) {
        	//System.out.println("!!!!");
        	StringBuffer sb = new StringBuffer();
        	for (int i = 0; i < numOfVexs; i++) {
        		//System.out.println("#");
				for (int j = i+1; j < numOfVexs; j++) {
//					sb.append(KSP_maxbandwidth(i, j, numOfVexs, nw.toIntTopo(0), k));
					if(type_ra == Type_RA.minC) {
						sb.append(KSP_minCost(i, j, numOfVexs, nw.toIntTopo(time), k));
					}else if(type_ra == Type_RA.maxB) {
						sb.append(KSP_maxbandwidth(i, j, numOfVexs, nw.toIntTopo(time), k));
					}
					
				}
			}
        	sb.append("0-0");
        	line = sb.toString();
        	FileWriter fw = new FileWriter(file.getName(), true);
    		BufferedWriter bw = new BufferedWriter(fw);
    		
    		
    		bw.write(type+"#"+k+"#"+type_ra+"\r\n"+line+"\r\n");
    		bw.close();
    		
        }
        bufferedReader.close();
        reader.close();
        is.close();
        
		return line;
	}

	//��������̬ѡ��·��
	public String getPathByR(Requirement Rs,int numOfVexs){
		StringBuffer sb = new StringBuffer();

		double[][] edge;
		for (Requirement requirement : Rs.list_requirement) {
			//�������·���������,�������ÿ������ʱ϶���ҳ�·��
			requirement.paths.clear();
			int s = requirement.s;
			int d = requirement.d;
			for (int i = requirement.earliest_start; i <= requirement.latest_end; i++) {
				edge = nw.toDoubleTopo(requirement.earliest_start);
				double[] path = dijkstra_maxbandwidth(s,d,numOfVexs,edge);
				StringBuffer paths = new StringBuffer();
				if (path != null){
					StringBuffer Path = new StringBuffer();
					for (int j = 0; j < path.length; j++) {
						if(j < path.length-1){
							Path.append((int)path[j]+"-");
						}
						if(j == path.length-1){
							Path.replace(Path.length()-1,Path.length(),",");
						}
						double b = path[path.length-1];
//						if (j < path.length-2){
//							edge[(int)path[j]][(int)path[j+1]] += b;
//							edge[(int)path[j+1]][(int)path[j]] += b;
//						}
					}
					paths.append(Path);
				}
				nw.info_nws.get(i).setPaths(paths.toString());
			}
//			edge = nw.info_nws.get(requirement.earliest_start).matrix_topology;
//			double[][] edge = nw.toDoubleTopo(requirement.earliest_start);
//			int s = requirement.s;
//			int d = requirement.d;
//			double[] path = dijkstra_maxbandwidth(s,d,numOfVexs,edge);
//			if (path != null){
//				StringBuffer Path = new StringBuffer();
//				for (int j = 0; j < path.length; j++) {
//					if(j < path.length-1){
//						Path.append((int)path[j]+"-");
//					}
//					if(j == path.length-1){
//						Path.replace(Path.length()-1,Path.length(),",");
//					}
//					double b = path[path.length-1];
//					if (j < path.length-2){
//						edge[(int)path[j]][(int)path[j+1]] += b;
//						edge[(int)path[j+1]][(int)path[j]] += b;
//					}
//				}
//				paths.append(Path);
//			}

		}
		return null;
	}
//��������ѡ��·��
	public int[] dijkstra_minCost(int s,int d,int numOfVexs,int edges_na[][],Requirement Rs){
		if (s < 0 || s >= numOfVexs) {
			throw new ArrayIndexOutOfBoundsException();
		}
		// Ĭ�ϳ�ʼΪfalse
		boolean[] st = new boolean[numOfVexs];
		// ���Դ�㵽������ľ���
		int[] distance = new int[numOfVexs];
		//ǰ���ڵ�
		int[] prev = new int[numOfVexs];
		int edges[][] = edges_na.clone();
		//���ʾ�������޴�
		for (int i = 0; i < numOfVexs; i++)
		{
			for (int j = i + 1; j < numOfVexs; j++) {
				if (edges[i][j] == 0) {
					edges[i][j] = Integer.MAX_VALUE;
					edges[j][i] = Integer.MAX_VALUE;
				}
			}
		}
		for (int i = 0; i < numOfVexs; i++) {
			distance[i] = edges[s][i];
			prev[i] = s;//����i��ǰ������Ϊ0
		}
		st[s] = true;//��s������Ϊ�ҵ����·��
		// �����Դ�㵽���ඥ������·��
		for (int i = 1; i < numOfVexs; ++i) {
			int min = Integer.MAX_VALUE;
			int index=0;
			// �Ƚϴ�Դ�㵽���ඥ���·������
			for (int j = 0; j < numOfVexs; ++j) {
				// ��Դ�㵽j��������·����û���ҵ�
				if (st[j]==false) {
					// ��Դ�㵽j�����·��������С
					if (distance[j] < min) {
						index = j;//index��¼��Դ�ڵ㵽j��������·��
						min = distance[j];
					}
				}
			}
			//�ҵ�Դ�㵽����Ϊindex��������·������
			if(index!=-1) {
				st[index] = true;
			}
			// ���µ�ǰ���·��������
			for (int w = 0; w < numOfVexs; w++) {
				if (st[w] == false) {
					if (edges[index][w] != Integer.MAX_VALUE
							&& (min + edges[index][w] < distance[w])) {
						distance[w] = min + edges[index][w];
						prev[w] = index;
					}
				}
			}
		}
		StringBuffer sb = new StringBuffer();
		String ss = "";
		Stack<Integer> stack = new Stack<Integer>();
		for(int i=0;i<=d;i++) {//�ҵ�Ŀ�Ľڵ�Ϊd�ľͽ���
			int j=i;
			if(j == s) {
				continue;
			}
			while(true) {
				stack.push(j);
				if(prev[j] == s || prev[j] == j || prev[j] == i) {
					break;
				}
				j = prev[j];
			}
			stack.push(s);
			while(!stack.empty()) {
				sb.append(stack.pop());
				if(!stack.empty()) {
					sb.append("-");
				}
			}
			if(i == d) {//��Ŀ�Ľڵ�Ϊd��
				if(s<d) {
					ss = sb.toString().split(",")[d-1];
				}else {
					ss = sb.toString().split(",")[d];
				}
			}
			sb.append(",");
		}
		int[] arr = new int[ss.split("-").length+1];
		for(int i=0;i<ss.split("-").length;i++) {
			arr[i] = Integer.valueOf(ss.split("-")[i]);
		}
		int minB = Integer.MAX_VALUE;
		for(int i=1;i<arr.length-1;i++) {
			if(edges[arr[i-1]][arr[i]] < minB) {
				minB = edges[arr[i-1]][arr[i]];
				System.out.println(arr[i-1]+"-"+arr[i]+" minB="+minB);
			}
		}
		arr[ss.split("-").length] = -(minB);//�����еĸ�����ʾ·����ֵ���෴��
		return arr;

	}
	/*
	 * dj�ɱ����
	 * 
	 */
	public int[] dijkstra_minCost(int s,int d,int numOfVexs,int edges_na[][]) {
		if (s < 0 || s >= numOfVexs) {
			throw new ArrayIndexOutOfBoundsException();
		}
		
		boolean[] st = new boolean[numOfVexs];// Ĭ�ϳ�ʼΪfalse
		int[] distance = new int[numOfVexs];// ���Դ�㵽������ľ���

		int[] prev = new int[numOfVexs];//ǰ���ڵ�

		int edges[][] = edges_na.clone();
		for (int i = 0; i < numOfVexs; i++)//���ʾ�������޴�
		{
			for (int j = i + 1; j < numOfVexs; j++) {
				if (edges[i][j] == 0) {
					edges[i][j] = Integer.MAX_VALUE;
					edges[j][i] = Integer.MAX_VALUE;
				}
			}
		}
		
		
		for (int i = 0; i < numOfVexs; i++) {
			distance[i] = edges[s][i];
			prev[i] = s;//����i��ǰ������Ϊ0
		}
		st[s] = true;//��s������Ϊ�ҵ����·��
		
		// �����Դ�㵽���ඥ������·��
		
		for (int i = 1; i < numOfVexs; ++i) {
			int min = Integer.MAX_VALUE;
			
			int index=0;
			// �Ƚϴ�Դ�㵽���ඥ���·������
			for (int j = 0; j < numOfVexs; ++j) {
				// ��Դ�㵽j��������·����û���ҵ�
				if (st[j]==false) {
					// ��Դ�㵽j�����·��������С
					if (distance[j] < min) {
//					if (distance[j] > max) {
						index = j;//index��¼��Դ�ڵ㵽j��������·��
						min = distance[j];
//						max = distance[j];
					}
				}
			}
			//�ҵ�Դ�㵽����Ϊindex��������·������
			if(index!=-1) {
				st[index] = true;
			}
			// ���µ�ǰ���·��������
			for (int w = 0; w < numOfVexs; w++) {
				if (st[w] == false) {
					if (edges[index][w] != Integer.MAX_VALUE
							&& (min + edges[index][w] < distance[w])) {
						distance[w] = min + edges[index][w];
						prev[w] = index;
					}


				}
			}
		}

		StringBuffer sb = new StringBuffer();
		String ss = "";
		Stack<Integer> stack = new Stack<Integer>();
		

		
		for(int i=0;i<=d;i++) {//�ҵ�Ŀ�Ľڵ�Ϊd�ľͽ���
			int j=i;
			if(j == s) {
				continue;
			}
			while(true) {
//				System.out.print(j+"<@-");
				stack.push(j);
				if(prev[j] == s || prev[j] == j || prev[j] == i) {
					break;
				}
				j = prev[j];
				
			}
			
			//System.out.println(s);
			stack.push(s);
			while(!stack.empty()) {
				sb.append(stack.pop());
				if(!stack.empty()) {
					sb.append("-");
				}
			}
			if(i == d) {//��Ŀ�Ľڵ�Ϊd��
				if(s<d) {
					ss = sb.toString().split(",")[d-1];
				}else {
					ss = sb.toString().split(",")[d];
				}
				
			}
			sb.append(",");
			
			


		}
			

		int[] arr = new int[ss.split("-").length+1];
		for(int i=0;i<ss.split("-").length;i++) {
			arr[i] = Integer.valueOf(ss.split("-")[i]);
		}
		
		int minB = Integer.MAX_VALUE;
		for(int i=1;i<arr.length-1;i++) {
			if(edges[arr[i-1]][arr[i]] < minB) {
				minB = edges[arr[i-1]][arr[i]];
				System.out.println(arr[i-1]+"-"+arr[i]+" minB="+minB);
			}
		}
		
		arr[ss.split("-").length] = -(minB);//�����еĸ�����ʾ·����ֵ���෴��
		//return ss;
		return arr;
		
	}
	
	
	/*
	 * dj����������
	 */
	public int[] dijkstra_maxbandwidth(int s,int d,int numOfVexs,int edges[][]) {
		if (s < 0 || s >= numOfVexs) {
			throw new ArrayIndexOutOfBoundsException();
		}
		
		boolean[] st = new boolean[numOfVexs];// Ĭ�ϳ�ʼΪfalse
		int[] distance = new int[numOfVexs];// ���Դ�㵽������ľ���
//		String[] path = new String[numOfVexs];// ���Դ�㵽������ľ���
//		for(int i=0;i<numOfVexs;i++){
//            path[i] = s+"->"+i;
//        }
		int[] prev = new int[numOfVexs];//ǰ���ڵ�
		//boolean[] flag = new boolean[numOfVexs];//��ʾ����v��������������·���Ƿ��Ѿ���ȡ
		
//		for (int i = 0; i < numOfVexs; i++)//���ʾ�������޴�
//			for (int j = i + 1; j < numOfVexs; j++) {
//				if (edges[i][j] == 0) {
////					edges[i][j] = Integer.MAX_VALUE;
////					edges[j][i] = Integer.MAX_VALUE;
//				}
//			}
		for (int i = 0; i < numOfVexs; i++) {
			distance[i] = edges[s][i];
			prev[i] = s;//����i��ǰ������Ϊ0
		}
		st[s] = true;//��s������Ϊ�ҵ����·��
		
		// �����Դ�㵽���ඥ������·��
		
		for (int i = 1; i < numOfVexs; ++i) {
//			int min = Integer.MAX_VALUE;
			int max = 0;
			int index=0;
			// �Ƚϴ�Դ�㵽���ඥ���·������
			for (int j = 0; j < numOfVexs; ++j) {
				// ��Դ�㵽j��������·����û���ҵ�
				if (st[j]==false) {
					// ��Դ�㵽j�����·��������С
//					if (distance[j] < min) {
					if (distance[j] > max) {
						index = j;//index��¼��Դ�ڵ㵽j��������·��
//						min = distance[j];
						max = distance[j];
					}
				}
			}
			//�ҵ�Դ�㵽����Ϊindex��������·������
			if(index!=-1) {
				st[index] = true;
			}
			// ���µ�ǰ���·��������
			for (int w = 0; w < numOfVexs; w++) {
				if (st[w] != true) {
//					if (edges[index][w] != Integer.MAX_VALUE
					if (edges[index][w] != 0
//							&& (min + edges[index][w] < distance[w])) {
							&& max > distance[w]
							&& edges[index][w] > distance[w]) {
//						distance[w] = min + edges[index][w];
						distance[w] = Math.min(max, edges[index][w]);
						prev[w] = index;
//						path[w] = path[index]+"->"+w;
//						if(w > s) {
//							prev[w+1] = index;
//						}else {
//							prev[w] = index;
//						}
						//prev[index] = w;
					}
				}
			}
		}
//		for(int i=0;i<path.length;i++) {
//			System.out.println(path[i]);
//		}
		
		StringBuffer sb = new StringBuffer();
		String ss = "";
		Stack<Integer> stack = new Stack<Integer>();
		
//		for(int i=0;i<prev.length;i++) {
//			System.out.println(prev[i]);
//		}
		
		for(int i=0;i<=d;i++) {//�ҵ�Ŀ�Ľڵ�Ϊd�ľͽ���
			int j=i;
			if(j == s) {
				continue;
			}
			while(true) {
//				System.out.print(j+"<@-");
				stack.push(j);
				if(prev[j] == s || prev[j] == j || prev[j] == i) {
					break;
				}
				j = prev[j];
				
			}
			
			//System.out.println(s);
			stack.push(s);
			while(!stack.empty()) {
				sb.append(stack.pop());
				if(!stack.empty()) {
					sb.append("-");
				}
			}
			if(i == d) {//��Ŀ�Ľڵ�Ϊd��
				if(s<d) {
					ss = sb.toString().split(",")[d-1];
				}else {
					ss = sb.toString().split(",")[d];
				}
				
			}
			sb.append(",");
			
			
			
//			List<Integer> path = new ArrayList<Integer>(); 
//			while(true) {
//				System.out.print(j+"<@-");
//				path.add(j);
//				if(j==0)
//					break;
//				j = prev[j];
//			}
//			for (int x = path.size()-1; x >= 0; x--) {
//                if (x == 0) {
//                    System.out.println(path.get(x));
//                } else {
//                    System.out.print(path.get(x) + "->");
//                }
//            }

		}
			
			
			
			
//		System.out.println();
//		System.out.println(sb);		
//		return distance;
		int[] arr = new int[ss.split("-").length+1];
		for(int i=0;i<ss.split("-").length;i++) {
			arr[i] = Integer.valueOf(ss.split("-")[i]);
		}
		arr[ss.split("-").length] = -distance[d];//�����еĸ�����ʾ·����ֵ���෴��
		//return ss;
		return arr;
		
	}
	

	/*
	 * dj������double
	 */
	public double[] dijkstra_maxbandwidth(int s,int d,int numOfVexs,double edges[][]) {
		if (s < 0 || s >= numOfVexs) {
			throw new ArrayIndexOutOfBoundsException();
		}
		
		boolean[] st = new boolean[numOfVexs];// Ĭ�ϳ�ʼΪfalse
		double[] distance = new double[numOfVexs];// ���Դ�㵽������ľ���

		int[] prev = new int[numOfVexs];//ǰ���ڵ�

		for (int i = 0; i < numOfVexs; i++) {
			distance[i] = edges[s][i];
			prev[i] = s;//����i��ǰ������Ϊ0
		}
		st[s] = true;//��s������Ϊ�ҵ����·��
		
		// �����Դ�㵽���ඥ������·��
		
		for (int i = 1; i < numOfVexs; ++i) {
//			int min = Integer.MAX_VALUE;
			double max = 0;
			int index=0;
			// �Ƚϴ�Դ�㵽���ඥ���·������
			for (int j = 0; j < numOfVexs; ++j) {
				// ��Դ�㵽j��������·����û���ҵ�
				if (st[j]==false) {
					// ��Դ�㵽j�����·��������С
//					if (distance[j] < min) {
					if (distance[j] > max) {
						index = j;//index��¼��Դ�ڵ㵽j��������·��
//						min = distance[j];
						max = distance[j];
					}
				}
			}
			//�ҵ�Դ�㵽����Ϊindex��������·������
			if(index!=-1)
				st[index] = true;
			// ���µ�ǰ���·��������
			for (int w = 0; w < numOfVexs; w++)
				if (st[w] != true) {
//					if (edges[index][w] != Integer.MAX_VALUE
					if (edges[index][w] != 0
//							&& (min + edges[index][w] < distance[w])) {
							&& max > distance[w]
							&& edges[index][w] > distance[w]) {
//						distance[w] = min + edges[index][w];
						distance[w] = Math.min(max, edges[index][w]);
						prev[w] = index;

					}				
				}
		}

		
		StringBuffer sb = new StringBuffer();
		String ss = "";
		Stack<Integer> stack = new Stack<Integer>();
		
		for(int i=0;i<=d;i++) {//�ҵ�Ŀ�Ľڵ�Ϊd�ľͽ���
			int j=i;
			if(j == s) {
				continue;
			}
			while(true) {
//				System.out.print(j+"<@-");
				stack.push(j);
				if(prev[j] == s || prev[j] == j || prev[j] == i) {
					break;
				}
				j = prev[j];
				
			}
			
			//System.out.println(s);
			stack.push(s);
			while(!stack.empty()) {
				sb.append(stack.pop());
				if(!stack.empty()) {
					sb.append("-");
				}
			}
			if(i == d) {//��Ŀ�Ľڵ�Ϊd��
				if(s<d) {
					ss = sb.toString().split(",")[d-1];
				}else {
					ss = sb.toString().split(",")[d];
				}
				
			}
			sb.append(",");
			
			
			

		}
			
			
			
			
//		System.out.println();
//		System.out.println(sb);		
//		return distance;
		double[] arr = new double[ss.split("-").length+1];
		for(int i=0;i<ss.split("-").length;i++) {
			arr[i] = Integer.valueOf(ss.split("-")[i]);
		}
		arr[ss.split("-").length] = -distance[d];//�����еĸ�����ʾ·����ֵ���෴��
		//return ss;
		return arr;
		
	}
	
	public Integer[] getIntegers(int[] arr) {
		Integer[] arr_I = new Integer[arr.length];
		for(int i=0;i<arr.length;i++) {
			arr_I[i] = arr[i];
		}
		return arr_I;
	}
	
	public Integer[] getIntegers(List<Integer> arr) {
		Integer[] arr_I = new Integer[arr.size()];
		for(int i=0;i<arr.size();i++) {
			arr_I[i] = arr.get(i);
		}
		return arr_I;
	}
	
	public List<Integer> getList(int[] arr) {
		List<Integer> arr_L = new ArrayList<Integer>();
		
		for(int i=0;i<arr.length;i++) {
			arr_L.add(arr[i]);
		}
		return arr_L;
	}
	
	public void printList(List<Integer> list) {
		int i = 0;
		for(Integer e:list) {
			if(i == list.size()-1) {
				System.out.print("����"+(-e));
				break;
			}
			System.out.print(e);
			i++;
			if(i < list.size()-1) {
				System.out.print("->");
			}
			
		}
		System.out.println();
	}
	public void printList(Integer[] list) {
		int i = 0;
		for(Integer e:list) {
			if(i == list.length-1) {
				System.out.print("����"+(-e));
				break;
			}
			System.out.print(e);
			i++;
			if(i < list.length-1) {
				System.out.print("->");
			}
			
		}
		System.out.println();
	}

	
	public boolean isContain(List<Integer[]> paths,List<Integer> path) {
		boolean havesame = false;
//		printList(path);
		for(Integer[] e:paths) {
			havesame = havesame || isSame(e, path);
			
		}
		return havesame;
	}
	
	public boolean isSame(Integer[] pathA,List<Integer> pathB) {
		if(pathA.length != pathB.size()) {
			return false;
		}
		
		for (int i = 0; i < pathA.length-1; i++) {
			if(pathA[i] != pathB.get(i)) {
				return false;
			}
		}
		return true;
	}

	//ǰk��������·��
	public String KSP_maxbandwidth(int s,int d,int numOfVexs,int edges[][],int k) {
		
//		int edgescopy[][] = edges.clone();//���һ���ڽӾ���ĸ���
//		int A[][] = new int[k][numOfVexs];
		List<Integer[]> paths_A = new ArrayList<Integer[]>();//�洢ȷ����k�����·��
//		List<PathInt[]> paths_A = new ArrayList<PathInt[]>();//�洢ȷ����k�����·��
		List<Integer[]> paths_B = new ArrayList<Integer[]>();//�洢Ԥ�������·��
		
		Integer[] root_path = getIntegers(dijkstra_maxbandwidth(s, d, numOfVexs, edges));
//		System.out.print("��һ��·����");
//		printList(root_path);//�ȴ�ӡ��һ��·��
		
		paths_A.add(root_path);//ԭ
//		paths_A.add(PathInt.toPI(root_path));
		
		for(int i=0;i<k-1;i++) {
			int edgescopy[][] = edges.clone();//���һ���ڽӾ���ĸ���

			Integer[] iteration_path = paths_A.get(i);//��i��·����Ϊ����·��,�����i+1��·�� //ԭ
//			Integer[] iteration_path = PathInt.toI(paths_A.get(i));//��i��·����Ϊ����·��,�����i+1��·��

			System.out.println();
			System.out.print("���ε���·��·����");
			printList(iteration_path);//��ӡ����·��
			for(int j=1;j<iteration_path.length-1;j++) {//��B���������·������ӵ���·����·����ƫ��·��������ظ�������
				List<Integer> path_B = new ArrayList<Integer>();
				int cost=0;//��ǰ�����ƫ��·���ĳɱ�
				for(int l=1;l<=j;l++) {//ƫ��ǰ�Ľڵ���빹��ƫ��·����l�ڵ㵽l-1�ڵ����·����ɾ��
					if(l-2 >= 0) {
						path_B.add(iteration_path[l-2]);
					}
					
					if(l != j) {
						cost += edges[iteration_path[l-1]][iteration_path[l]];//��û�б�Ĳ���·����cost����
					}
				}
				int tem_ca = edgescopy[iteration_path[j-1]][iteration_path[j]];
				edgescopy[iteration_path[j-1]][iteration_path[j]] = 0;//ɾ��·��
				edgescopy[iteration_path[j]][iteration_path[j-1]] = 0;//ɾ��·��
//				System.out.println("ɾ����"+iteration_path[j-1]+"-"+iteration_path[j]);
//				dijkstra_maxbandwidth(iteration_path[j], d, numOfVexs, edgescopy);
				int[] tem = dijkstra_maxbandwidth(iteration_path[j-1], d, numOfVexs, edgescopy);
//				tem[tem.length-1] = tem[tem.length-1] - cost - edgescopy[iteration_path[j-1]][tem[0]];//����ƫ��·���ĳɱ� 
//				System.out.println("��"+iteration_path[j-1]+"��"+d+"��·��");
//				System.out.println("����·���ĳɱ���"+(-iteration_path[iteration_path.length-1]));
//				System.out.println("����ƫ��·���ĳɱ���"+(-tem[tem.length-1]));
//				printList(getList(tem));
				tem[tem.length-1] = -Math.min(-tem[tem.length-1], -iteration_path[iteration_path.length-1]);
				boolean haveloop = false;
				for(Integer eB:path_B) {//�����л�
					for(int e:tem) {
						if(eB.equals(e)) {
							haveloop = true;
							break;
						}
					}
					
				}
				
				path_B.addAll(getList(tem));//path_BΪ�����ɵ�ƫ��·��
				System.out.print("	Ԥ��ƫ��·����");
				printList(path_B);
				System.out.println("	�Ƿ��л���"+haveloop+" �Ƿ���paths_A�������"+isContain(paths_A, path_B)+" �Ƿ���paths_B�������"+isContain(paths_B, path_B)+" path_B�Ĵ���Ϊ��"+(-path_B.get(path_B.size()-1)));
				if(!haveloop && !isContain(paths_A, path_B)&& !isContain(paths_B, path_B) && path_B.get(path_B.size()-1)!=0) { //ԭ
//				if(!paths_A.contains(PathInt.toPI(getIntegers(path_B)))) {
					paths_B.add(getIntegers(path_B));//��ƫ��·�����뼯��B
//					System.out.print(!isContain(paths_A, path_B));
					System.out.print("<��>�����ƫ��·����");
					printList(path_B);
				}
				
				
				
				System.out.println("��ԭ�ģ�"+tem_ca);
//				edgescopy[iteration_path[j-1]][iteration_path[j]] = tem_ca;//��ԭ·��
//				edgescopy[iteration_path[j]][iteration_path[j-1]] = tem_ca;//��ԭ·��
			}
			int maxB = 0;
			Integer[] minB_path = null;
			if(paths_B.size() == 0) {
				System.out.println("��·��");
				break;
			}
			//��ƫ��·��������ѡ������·��
			
			for(Integer[] e:paths_B) {
				if(e.length > maxB) {
					maxB = e.length;
					minB_path = e;
				}
			}
			if(minB_path != null) {
				
				System.out.print("���ε���ѡ����·����");
//				System.out.println("ȡ������ѡ����·��ǰ��paths_B��Ԫ��������"+paths_B.size());

				printList(minB_path);
				paths_A.add(minB_path); //ԭ

//				paths_A.add(PathInt.toPI(minB_path));
				paths_B.remove(minB_path);
//				System.out.println("ȡ������ѡ����·����paths_B��Ԫ��������"+paths_B.size());
//				for(Integer[] e:paths_B) {
//					printList(e);
//				}
			}
			
			
		}
		
		StringBuffer sb = new StringBuffer();
		System.out.println("���"); 
		for(Integer[] e:paths_A) {
			printList(e);
			for (int i = 0; i < e.length; i++) {
				if(i == e.length-1) {
					sb.append(",");
				}else if(i == e.length-2){
					sb.append(e[i]);
				}else {
					sb.append(e[i]+"-");
				}
				
			}
		}
		
		
		return sb.toString();
	}

//	ǰk���ɱ����·��
	public String KSP_minCost(int s,int d,int numOfVexs,int edges[][],int k) {
		

		List<Integer[]> paths_A = new ArrayList<Integer[]>();//�洢ȷ����k�����·��
//		List<PathInt[]> paths_A = new ArrayList<PathInt[]>();//�洢ȷ����k�����·��
		List<Integer[]> paths_B = new ArrayList<Integer[]>();//�洢Ԥ�������·��
		
		Integer[] root_path = getIntegers(dijkstra_minCost(s, d, numOfVexs, edges));
//		System.out.print("��һ��·����");
//		printList(root_path);//�ȴ�ӡ��һ��·��
		
		paths_A.add(root_path);//ԭ
//		paths_A.add(PathInt.toPI(root_path));
		
		for(int i=0;i<k-1;i++) {
			int edgescopy[][] = edges.clone();//���һ���ڽӾ���ĸ���

			Integer[] iteration_path = paths_A.get(i);//��i��·����Ϊ����·��,�����i+1��·�� //ԭ
//			Integer[] iteration_path = PathInt.toI(paths_A.get(i));//��i��·����Ϊ����·��,�����i+1��·��

			System.out.println();
			System.out.print("���ε���·��·����");
			printList(iteration_path);//��ӡ����·��
			for(int j=1;j<iteration_path.length-1;j++) {//��B���������·������ӵ���·����·����ƫ��·��������ظ�������
				List<Integer> path_B = new ArrayList<Integer>();
				int cost=0;//��ǰ�����ƫ��·���ĳɱ�
				for(int l=1;l<=j;l++) {//ƫ��ǰ�Ľڵ���빹��ƫ��·����l�ڵ㵽l-1�ڵ����·����ɾ��
					if(l-2 >= 0) {
						path_B.add(iteration_path[l-2]);
					}
					
					if(l != j) {
						cost += edges[iteration_path[l-1]][iteration_path[l]];//��û�б�Ĳ���·����cost����
					}
				}
				int tem_ca = edgescopy[iteration_path[j-1]][iteration_path[j]];
				edgescopy[iteration_path[j-1]][iteration_path[j]] = 0;//ɾ��·��
				edgescopy[iteration_path[j]][iteration_path[j-1]] = 0;//ɾ��·��
//				System.out.println("ɾ����"+iteration_path[j-1]+"-"+iteration_path[j]);
//				dijkstra_maxbandwidth(iteration_path[j], d, numOfVexs, edgescopy);
				int[] tem = dijkstra_minCost(iteration_path[j-1], d, numOfVexs, edgescopy);
//				tem[tem.length-1] = tem[tem.length-1] - cost - edgescopy[iteration_path[j-1]][tem[0]];//����ƫ��·���ĳɱ� 
//				System.out.println("��"+iteration_path[j-1]+"��"+d+"��·��");
//				System.out.println("����·���ĳɱ���"+(-iteration_path[iteration_path.length-1]));
//				System.out.println("����ƫ��·���ĳɱ���"+(-tem[tem.length-1]));
//				printList(getList(tem));
				tem[tem.length-1] = -Math.min(-tem[tem.length-1], -iteration_path[iteration_path.length-1]);
				boolean haveloop = false;
				for(Integer eB:path_B) {//�����л�
					for(int e:tem) {
						if(eB.equals(e)) {
							haveloop = true;
							break;
						}
					}
					
				}
				
				path_B.addAll(getList(tem));//path_BΪ�����ɵ�ƫ��·��
				System.out.print("	Ԥ��ƫ��·����");
				printList(path_B);
				System.out.println("	�Ƿ��л���"+haveloop+" �Ƿ���paths_A�������"+isContain(paths_A, path_B)+" �Ƿ���paths_B�������"+isContain(paths_B, path_B)+" path_B�Ĵ���Ϊ��"+(-path_B.get(path_B.size()-1)));
				if(!haveloop && !isContain(paths_A, path_B)&& !isContain(paths_B, path_B) && path_B.get(path_B.size()-1)!=0) { //ԭ
//				if(!paths_A.contains(PathInt.toPI(getIntegers(path_B)))) {
					paths_B.add(getIntegers(path_B));//��ƫ��·�����뼯��B
//					System.out.print(!isContain(paths_A, path_B));
					System.out.print("<��>�����ƫ��·����");
					printList(path_B);
				}
				
				
				
				System.out.println("��ԭ�ģ�"+tem_ca);
//				edgescopy[iteration_path[j-1]][iteration_path[j]] = tem_ca;//��ԭ·��
//				edgescopy[iteration_path[j]][iteration_path[j-1]] = tem_ca;//��ԭ·��
			}
			int maxB = 0;
			Integer[] minB_path = null;
			if(paths_B.size() == 0) {
				System.out.println("��·��");
				break;
			}
			//��ƫ��·��������ѡ������·��
			
			for(Integer[] e:paths_B) {
				if(e.length > maxB) {
					maxB = e.length;
					minB_path = e;
				}
			}
			if(minB_path != null) {
				
				System.out.print("���ε���ѡ����·����");
//				System.out.println("ȡ������ѡ����·��ǰ��paths_B��Ԫ��������"+paths_B.size());

				printList(minB_path);
				paths_A.add(minB_path); //ԭ

//				paths_A.add(PathInt.toPI(minB_path));
				paths_B.remove(minB_path);
//				System.out.println("ȡ������ѡ����·����paths_B��Ԫ��������"+paths_B.size());
//				for(Integer[] e:paths_B) {
//					printList(e);
//				}
			}
			
			
		}
		
		StringBuffer sb = new StringBuffer();
		System.out.println("���"); 
		for(Integer[] e:paths_A) {
			printList(e);
			for (int i = 0; i < e.length; i++) {
				if(i == e.length-1) {
					sb.append(",");
				}else if(i == e.length-2){
					sb.append(e[i]);
				}else {
					sb.append(e[i]+"-");
				}
				
			}
		}
		
		
		return sb.toString();
	}


	public static void main(String[] args) {
		NetworkTest nw = new NetworkTest(NetworkTest.Type.size1,1,10);
		RoutingAlgorithm ra = new RoutingAlgorithm(nw);
		int[][] topo = nw.toIntTopo(0);
//		int[][] topo = {
//				{0,10,15,0},
//				{10,0,30,20},
//				{15,30,0,20},
//				{0,20,20,0}
//		};
//		int[] a = dijkstra(0,3,topo.length,topo);
//		int[] a = dijkstra_maxbandwidth(0,3,topo.length,topo);
//		String s = dijkstra_maxbandwidth(0,3,topo.length,topo);
		
//		int[] arr = dijkstra_maxbandwidth(0,3,topo.length,topo);
//		ra.getPathByR()
		double[][] topod = nw.toDoubleTopo(0);
		double[] arr = ra.dijkstra_maxbandwidth(0,3,topo.length,topod);
//
//		for(int e:arr) {
//			System.out.println(e);
//		}
		for(double e:arr) {
		System.out.println(e);
	}
		
//		printList(getIntegers(dijkstra_maxbandwidth(0,1,topo.length,topo)));
//		String s = KSP_minCost(0, 1, topo.length, topo, 6);
//		System.out.println(s);
	}

}
enum Type_RA{minC,maxB};