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
		File file = new File("默认路径.txt");
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
            	System.out.println("有缓存");
            	line = bufferedReader.readLine();
            	break;
            }


        }
		//如果文件中没有
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

	//根据请求动态选择路径
	public String getPathByR(Requirement Rs,int numOfVexs){
		StringBuffer sb = new StringBuffer();

		double[][] edge;
		for (Requirement requirement : Rs.list_requirement) {
			//将请求的路径集合清空,从请求的每个可用时隙中找出路径
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
//根据请求选择路径
	public int[] dijkstra_minCost(int s,int d,int numOfVexs,int edges_na[][],Requirement Rs){
		if (s < 0 || s >= numOfVexs) {
			throw new ArrayIndexOutOfBoundsException();
		}
		// 默认初始为false
		boolean[] st = new boolean[numOfVexs];
		// 存放源点到其他点的矩离
		int[] distance = new int[numOfVexs];
		//前驱节点
		int[] prev = new int[numOfVexs];
		int edges[][] = edges_na.clone();
		//零表示距离无限大
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
			prev[i] = s;//顶点i的前驱顶点为0
		}
		st[s] = true;//将s顶点标记为找到最短路径
		// 处理从源点到其余顶点的最短路径
		for (int i = 1; i < numOfVexs; ++i) {
			int min = Integer.MAX_VALUE;
			int index=0;
			// 比较从源点到其余顶点的路径长度
			for (int j = 0; j < numOfVexs; ++j) {
				// 从源点到j顶点的最短路径还没有找到
				if (st[j]==false) {
					// 从源点到j顶点的路径长度最小
					if (distance[j] < min) {
						index = j;//index记录，源节点到j顶点的最短路径
						min = distance[j];
					}
				}
			}
			//找到源点到索引为index顶点的最短路径长度
			if(index!=-1) {
				st[index] = true;
			}
			// 更新当前最短路径及距离
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
		for(int i=0;i<=d;i++) {//找到目的节点为d的就结束
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
			if(i == d) {//把目的节点为d的
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
		arr[ss.split("-").length] = -(minB);//数组中的负数表示路径的值的相反数
		return arr;

	}
	/*
	 * dj成本最低
	 * 
	 */
	public int[] dijkstra_minCost(int s,int d,int numOfVexs,int edges_na[][]) {
		if (s < 0 || s >= numOfVexs) {
			throw new ArrayIndexOutOfBoundsException();
		}
		
		boolean[] st = new boolean[numOfVexs];// 默认初始为false
		int[] distance = new int[numOfVexs];// 存放源点到其他点的矩离

		int[] prev = new int[numOfVexs];//前驱节点

		int edges[][] = edges_na.clone();
		for (int i = 0; i < numOfVexs; i++)//零表示距离无限大
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
			prev[i] = s;//顶点i的前驱顶点为0
		}
		st[s] = true;//将s顶点标记为找到最短路径
		
		// 处理从源点到其余顶点的最短路径
		
		for (int i = 1; i < numOfVexs; ++i) {
			int min = Integer.MAX_VALUE;
			
			int index=0;
			// 比较从源点到其余顶点的路径长度
			for (int j = 0; j < numOfVexs; ++j) {
				// 从源点到j顶点的最短路径还没有找到
				if (st[j]==false) {
					// 从源点到j顶点的路径长度最小
					if (distance[j] < min) {
//					if (distance[j] > max) {
						index = j;//index记录，源节点到j顶点的最短路径
						min = distance[j];
//						max = distance[j];
					}
				}
			}
			//找到源点到索引为index顶点的最短路径长度
			if(index!=-1) {
				st[index] = true;
			}
			// 更新当前最短路径及距离
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
		

		
		for(int i=0;i<=d;i++) {//找到目的节点为d的就结束
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
			if(i == d) {//把目的节点为d的
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
		
		arr[ss.split("-").length] = -(minB);//数组中的负数表示路径的值的相反数
		//return ss;
		return arr;
		
	}
	
	
	/*
	 * dj最大带宽，整数
	 */
	public int[] dijkstra_maxbandwidth(int s,int d,int numOfVexs,int edges[][]) {
		if (s < 0 || s >= numOfVexs) {
			throw new ArrayIndexOutOfBoundsException();
		}
		
		boolean[] st = new boolean[numOfVexs];// 默认初始为false
		int[] distance = new int[numOfVexs];// 存放源点到其他点的矩离
//		String[] path = new String[numOfVexs];// 存放源点到其他点的矩离
//		for(int i=0;i<numOfVexs;i++){
//            path[i] = s+"->"+i;
//        }
		int[] prev = new int[numOfVexs];//前驱节点
		//boolean[] flag = new boolean[numOfVexs];//表示顶点v到各个定点的最短路径是否已经获取
		
//		for (int i = 0; i < numOfVexs; i++)//零表示距离无限大
//			for (int j = i + 1; j < numOfVexs; j++) {
//				if (edges[i][j] == 0) {
////					edges[i][j] = Integer.MAX_VALUE;
////					edges[j][i] = Integer.MAX_VALUE;
//				}
//			}
		for (int i = 0; i < numOfVexs; i++) {
			distance[i] = edges[s][i];
			prev[i] = s;//顶点i的前驱顶点为0
		}
		st[s] = true;//将s顶点标记为找到最短路径
		
		// 处理从源点到其余顶点的最短路径
		
		for (int i = 1; i < numOfVexs; ++i) {
//			int min = Integer.MAX_VALUE;
			int max = 0;
			int index=0;
			// 比较从源点到其余顶点的路径长度
			for (int j = 0; j < numOfVexs; ++j) {
				// 从源点到j顶点的最短路径还没有找到
				if (st[j]==false) {
					// 从源点到j顶点的路径长度最小
//					if (distance[j] < min) {
					if (distance[j] > max) {
						index = j;//index记录，源节点到j顶点的最短路径
//						min = distance[j];
						max = distance[j];
					}
				}
			}
			//找到源点到索引为index顶点的最短路径长度
			if(index!=-1) {
				st[index] = true;
			}
			// 更新当前最短路径及距离
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
		
		for(int i=0;i<=d;i++) {//找到目的节点为d的就结束
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
			if(i == d) {//把目的节点为d的
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
		arr[ss.split("-").length] = -distance[d];//数组中的负数表示路径的值的相反数
		//return ss;
		return arr;
		
	}
	

	/*
	 * dj最大带宽，double
	 */
	public double[] dijkstra_maxbandwidth(int s,int d,int numOfVexs,double edges[][]) {
		if (s < 0 || s >= numOfVexs) {
			throw new ArrayIndexOutOfBoundsException();
		}
		
		boolean[] st = new boolean[numOfVexs];// 默认初始为false
		double[] distance = new double[numOfVexs];// 存放源点到其他点的矩离

		int[] prev = new int[numOfVexs];//前驱节点

		for (int i = 0; i < numOfVexs; i++) {
			distance[i] = edges[s][i];
			prev[i] = s;//顶点i的前驱顶点为0
		}
		st[s] = true;//将s顶点标记为找到最短路径
		
		// 处理从源点到其余顶点的最短路径
		
		for (int i = 1; i < numOfVexs; ++i) {
//			int min = Integer.MAX_VALUE;
			double max = 0;
			int index=0;
			// 比较从源点到其余顶点的路径长度
			for (int j = 0; j < numOfVexs; ++j) {
				// 从源点到j顶点的最短路径还没有找到
				if (st[j]==false) {
					// 从源点到j顶点的路径长度最小
//					if (distance[j] < min) {
					if (distance[j] > max) {
						index = j;//index记录，源节点到j顶点的最短路径
//						min = distance[j];
						max = distance[j];
					}
				}
			}
			//找到源点到索引为index顶点的最短路径长度
			if(index!=-1)
				st[index] = true;
			// 更新当前最短路径及距离
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
		
		for(int i=0;i<=d;i++) {//找到目的节点为d的就结束
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
			if(i == d) {//把目的节点为d的
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
		arr[ss.split("-").length] = -distance[d];//数组中的负数表示路径的值的相反数
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
				System.out.print("带宽："+(-e));
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
				System.out.print("带宽："+(-e));
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

	//前k的最大带宽路径
	public String KSP_maxbandwidth(int s,int d,int numOfVexs,int edges[][],int k) {
		
//		int edgescopy[][] = edges.clone();//获得一个邻接矩阵的复制
//		int A[][] = new int[k][numOfVexs];
		List<Integer[]> paths_A = new ArrayList<Integer[]>();//存储确定的k条最短路径
//		List<PathInt[]> paths_A = new ArrayList<PathInt[]>();//存储确定的k条最短路径
		List<Integer[]> paths_B = new ArrayList<Integer[]>();//存储预备的最短路径
		
		Integer[] root_path = getIntegers(dijkstra_maxbandwidth(s, d, numOfVexs, edges));
//		System.out.print("第一条路径：");
//		printList(root_path);//先打印第一条路径
		
		paths_A.add(root_path);//原
//		paths_A.add(PathInt.toPI(root_path));
		
		for(int i=0;i<k-1;i++) {
			int edgescopy[][] = edges.clone();//获得一个邻接矩阵的复制

			Integer[] iteration_path = paths_A.get(i);//第i个路径作为迭代路径,求出第i+1条路径 //原
//			Integer[] iteration_path = PathInt.toI(paths_A.get(i));//第i个路径作为迭代路径,求出第i+1条路径

			System.out.println();
			System.out.print("本次迭代路径路径：");
			printList(iteration_path);//打印迭代路径
			for(int j=1;j<iteration_path.length-1;j++) {//向B集合中添加路径，添加迭代路径链路数的偏离路径，如果重复不加入
				List<Integer> path_B = new ArrayList<Integer>();
				int cost=0;//当前构造的偏离路径的成本
				for(int l=1;l<=j;l++) {//偏离前的节点加入构造偏离路径，l节点到l-1节点的链路将被删除
					if(l-2 >= 0) {
						path_B.add(iteration_path[l-2]);
					}
					
					if(l != j) {
						cost += edges[iteration_path[l-1]][iteration_path[l]];//把没有变的部分路径的cost加上
					}
				}
				int tem_ca = edgescopy[iteration_path[j-1]][iteration_path[j]];
				edgescopy[iteration_path[j-1]][iteration_path[j]] = 0;//删除路径
				edgescopy[iteration_path[j]][iteration_path[j-1]] = 0;//删除路径
//				System.out.println("删除了"+iteration_path[j-1]+"-"+iteration_path[j]);
//				dijkstra_maxbandwidth(iteration_path[j], d, numOfVexs, edgescopy);
				int[] tem = dijkstra_maxbandwidth(iteration_path[j-1], d, numOfVexs, edgescopy);
//				tem[tem.length-1] = tem[tem.length-1] - cost - edgescopy[iteration_path[j-1]][tem[0]];//更新偏离路径的成本 
//				System.out.println("找"+iteration_path[j-1]+"到"+d+"的路径");
//				System.out.println("迭代路径的成本："+(-iteration_path[iteration_path.length-1]));
//				System.out.println("部分偏离路径的成本："+(-tem[tem.length-1]));
//				printList(getList(tem));
				tem[tem.length-1] = -Math.min(-tem[tem.length-1], -iteration_path[iteration_path.length-1]);
				boolean haveloop = false;
				for(Integer eB:path_B) {//不能有环
					for(int e:tem) {
						if(eB.equals(e)) {
							haveloop = true;
							break;
						}
					}
					
				}
				
				path_B.addAll(getList(tem));//path_B为新生成的偏离路径
				System.out.print("	预备偏离路径：");
				printList(path_B);
				System.out.println("	是否有环："+haveloop+" 是否在paths_A里包含："+isContain(paths_A, path_B)+" 是否在paths_B里包含："+isContain(paths_B, path_B)+" path_B的带宽为："+(-path_B.get(path_B.size()-1)));
				if(!haveloop && !isContain(paths_A, path_B)&& !isContain(paths_B, path_B) && path_B.get(path_B.size()-1)!=0) { //原
//				if(!paths_A.contains(PathInt.toPI(getIntegers(path_B)))) {
					paths_B.add(getIntegers(path_B));//将偏离路径加入集合B
//					System.out.print(!isContain(paths_A, path_B));
					System.out.print("<新>加入的偏离路径：");
					printList(path_B);
				}
				
				
				
				System.out.println("还原的："+tem_ca);
//				edgescopy[iteration_path[j-1]][iteration_path[j]] = tem_ca;//还原路径
//				edgescopy[iteration_path[j]][iteration_path[j-1]] = tem_ca;//还原路径
			}
			int maxB = 0;
			Integer[] minB_path = null;
			if(paths_B.size() == 0) {
				System.out.println("无路径");
				break;
			}
			//从偏离路径集合中选出最大的路径
			
			for(Integer[] e:paths_B) {
				if(e.length > maxB) {
					maxB = e.length;
					minB_path = e;
				}
			}
			if(minB_path != null) {
				
				System.out.print("本次迭代选出的路径：");
//				System.out.println("取出本次选出的路径前，paths_B中元素数量："+paths_B.size());

				printList(minB_path);
				paths_A.add(minB_path); //原

//				paths_A.add(PathInt.toPI(minB_path));
				paths_B.remove(minB_path);
//				System.out.println("取出本次选出的路径后，paths_B中元素数量："+paths_B.size());
//				for(Integer[] e:paths_B) {
//					printList(e);
//				}
			}
			
			
		}
		
		StringBuffer sb = new StringBuffer();
		System.out.println("结果"); 
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

//	前k个成本最低路径
	public String KSP_minCost(int s,int d,int numOfVexs,int edges[][],int k) {
		

		List<Integer[]> paths_A = new ArrayList<Integer[]>();//存储确定的k条最短路径
//		List<PathInt[]> paths_A = new ArrayList<PathInt[]>();//存储确定的k条最短路径
		List<Integer[]> paths_B = new ArrayList<Integer[]>();//存储预备的最短路径
		
		Integer[] root_path = getIntegers(dijkstra_minCost(s, d, numOfVexs, edges));
//		System.out.print("第一条路径：");
//		printList(root_path);//先打印第一条路径
		
		paths_A.add(root_path);//原
//		paths_A.add(PathInt.toPI(root_path));
		
		for(int i=0;i<k-1;i++) {
			int edgescopy[][] = edges.clone();//获得一个邻接矩阵的复制

			Integer[] iteration_path = paths_A.get(i);//第i个路径作为迭代路径,求出第i+1条路径 //原
//			Integer[] iteration_path = PathInt.toI(paths_A.get(i));//第i个路径作为迭代路径,求出第i+1条路径

			System.out.println();
			System.out.print("本次迭代路径路径：");
			printList(iteration_path);//打印迭代路径
			for(int j=1;j<iteration_path.length-1;j++) {//向B集合中添加路径，添加迭代路径链路数的偏离路径，如果重复不加入
				List<Integer> path_B = new ArrayList<Integer>();
				int cost=0;//当前构造的偏离路径的成本
				for(int l=1;l<=j;l++) {//偏离前的节点加入构造偏离路径，l节点到l-1节点的链路将被删除
					if(l-2 >= 0) {
						path_B.add(iteration_path[l-2]);
					}
					
					if(l != j) {
						cost += edges[iteration_path[l-1]][iteration_path[l]];//把没有变的部分路径的cost加上
					}
				}
				int tem_ca = edgescopy[iteration_path[j-1]][iteration_path[j]];
				edgescopy[iteration_path[j-1]][iteration_path[j]] = 0;//删除路径
				edgescopy[iteration_path[j]][iteration_path[j-1]] = 0;//删除路径
//				System.out.println("删除了"+iteration_path[j-1]+"-"+iteration_path[j]);
//				dijkstra_maxbandwidth(iteration_path[j], d, numOfVexs, edgescopy);
				int[] tem = dijkstra_minCost(iteration_path[j-1], d, numOfVexs, edgescopy);
//				tem[tem.length-1] = tem[tem.length-1] - cost - edgescopy[iteration_path[j-1]][tem[0]];//更新偏离路径的成本 
//				System.out.println("找"+iteration_path[j-1]+"到"+d+"的路径");
//				System.out.println("迭代路径的成本："+(-iteration_path[iteration_path.length-1]));
//				System.out.println("部分偏离路径的成本："+(-tem[tem.length-1]));
//				printList(getList(tem));
				tem[tem.length-1] = -Math.min(-tem[tem.length-1], -iteration_path[iteration_path.length-1]);
				boolean haveloop = false;
				for(Integer eB:path_B) {//不能有环
					for(int e:tem) {
						if(eB.equals(e)) {
							haveloop = true;
							break;
						}
					}
					
				}
				
				path_B.addAll(getList(tem));//path_B为新生成的偏离路径
				System.out.print("	预备偏离路径：");
				printList(path_B);
				System.out.println("	是否有环："+haveloop+" 是否在paths_A里包含："+isContain(paths_A, path_B)+" 是否在paths_B里包含："+isContain(paths_B, path_B)+" path_B的带宽为："+(-path_B.get(path_B.size()-1)));
				if(!haveloop && !isContain(paths_A, path_B)&& !isContain(paths_B, path_B) && path_B.get(path_B.size()-1)!=0) { //原
//				if(!paths_A.contains(PathInt.toPI(getIntegers(path_B)))) {
					paths_B.add(getIntegers(path_B));//将偏离路径加入集合B
//					System.out.print(!isContain(paths_A, path_B));
					System.out.print("<新>加入的偏离路径：");
					printList(path_B);
				}
				
				
				
				System.out.println("还原的："+tem_ca);
//				edgescopy[iteration_path[j-1]][iteration_path[j]] = tem_ca;//还原路径
//				edgescopy[iteration_path[j]][iteration_path[j-1]] = tem_ca;//还原路径
			}
			int maxB = 0;
			Integer[] minB_path = null;
			if(paths_B.size() == 0) {
				System.out.println("无路径");
				break;
			}
			//从偏离路径集合中选出最大的路径
			
			for(Integer[] e:paths_B) {
				if(e.length > maxB) {
					maxB = e.length;
					minB_path = e;
				}
			}
			if(minB_path != null) {
				
				System.out.print("本次迭代选出的路径：");
//				System.out.println("取出本次选出的路径前，paths_B中元素数量："+paths_B.size());

				printList(minB_path);
				paths_A.add(minB_path); //原

//				paths_A.add(PathInt.toPI(minB_path));
				paths_B.remove(minB_path);
//				System.out.println("取出本次选出的路径后，paths_B中元素数量："+paths_B.size());
//				for(Integer[] e:paths_B) {
//					printList(e);
//				}
			}
			
			
		}
		
		StringBuffer sb = new StringBuffer();
		System.out.println("结果"); 
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