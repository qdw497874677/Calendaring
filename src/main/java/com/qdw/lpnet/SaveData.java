package com.qdw.lpnet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class SaveData {
	
	static public void save(String s) throws IOException {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		File file = new File("LP实验数据-详细.txt");
		if(!file.exists()){
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getName(), true);
		BufferedWriter bw = new BufferedWriter(fw);
		s += "\r\n";
		bw.write(df.format(new Date())+"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"+"\r\n");
		bw.write(s);
		bw.write("\r\n");
		bw.close();
	}
	
	static public void saveSome(String s) throws IOException {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		File file = new File("LP实验数据-部分.txt");
		if(!file.exists()){
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getName(), true);
		BufferedWriter bw = new BufferedWriter(fw);
		s += "\r\n";
		bw.write(df.format(new Date())+"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"+"\r\n");
		bw.write(s);
		bw.write("\r\n");
		bw.close();
	}
	
	static public void save_finally(String s) throws IOException {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		File file = new File("LP实验数据-最终.txt");
		if(!file.exists()){
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getName(), true);
		BufferedWriter bw = new BufferedWriter(fw);
		
		bw.write(df.format(new Date())+"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"+"\r\n");
		bw.write(s);
		bw.write("\r\n");
		bw.close();
	}
	
	static public void saveTest(String s) throws IOException {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		File file = new File("Test.txt");
		if(!file.exists()){
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getName(), true);
		BufferedWriter bw = new BufferedWriter(fw);
		s += "\r\n";
		bw.write(df.format(new Date())+"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"+"\r\n");
		bw.write(s);
		bw.write("\r\n");
		bw.close();
	}
	static public void saveTest(List<String> ss) throws IOException {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		File file = new File("Test.txt");
		if(!file.exists()){
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getName(), true);
		BufferedWriter bw = new BufferedWriter(fw);
		StringBuffer s = new StringBuffer();
		for(String e:ss) {
			s.append(e+"\r\n");
		}
		
		bw.write(df.format(new Date())+"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"+"\r\n");
		bw.write(s.toString());
		bw.write("\r\n");
		bw.close();
	}
}
