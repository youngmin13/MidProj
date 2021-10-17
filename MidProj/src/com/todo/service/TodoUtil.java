package com.todo.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.PreparedStatement;
import java.util.*;

import com.google.gson.Gson;
import com.todo.dao.TodoItem;
import com.todo.dao.TodoList;

public class TodoUtil {
	
	static Date time;
	String filename = "todolist.txt";
	
	public static void saveList(TodoList l, String filename)
	{
		try {
			Writer w = new FileWriter(filename);
			
			for (TodoItem item : l.getList())
			{
				w.write(item.toSaveString());
			}
			
			w.close();
			
			System.out.println("모든 데이터가 저장되었습니다.");
		} catch (FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void saveList_json(TodoList l, String filename)
	{
		List<TodoItem> json_item = new ArrayList<TodoItem>();
		Gson gson = new Gson();
		
		for (TodoItem item : l.getList())
		{
			json_item.add(item);
		}
		
		String jsonstr = gson.toJson(json_item);
		//System.out.println(jsonstr);
		
		try {
			FileWriter w = new FileWriter(filename);
			w.write(jsonstr);
			w.close();
			System.out.println("Json 데이터가 저장되었습니다.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void loadList_json(TodoList l, String filename)
	{
		Gson gson = new Gson();
		String jsonstr2 = null;
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			jsonstr2 = br.readLine();
			br.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		TodoItem[] array = gson.fromJson(jsonstr2, TodoItem[].class);
		List<TodoItem> list1 = Arrays.asList(array);
		System.out.println("JsonData: " + list1);
		
	}
	
	public static void loadList(TodoList l, String filename)
	{
		int count = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
		
			String oneline;
			
			while ((oneline = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(oneline, "##");
				String title = st.nextToken();
				String desc = st.nextToken();
				String category = st.nextToken();
				String due_date = st.nextToken();
				String current_date = st.nextToken();
				
				TodoItem s = new TodoItem(title, desc, category, current_date, due_date);
				
				l.addItem(s);
				
				count++;
			}
			br.close();
			if(count != 0)
				System.out.println(count + "개 항목을 읽었습니다.");
			else
				System.out.println(filename + "파일에 저장된 항목이 없습니다.");
			
		} catch (FileNotFoundException e) {
			System.out.println(filename + "파일이 없습니다.");
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void createItem(TodoList l) {
		
		String category, title, desc, due_date, place;
		Scanner sc = new Scanner(System.in);
		
		System.out.println("[항목 추가]");
		
		
		System.out.print("제목 > ");
		
		title = sc.nextLine();
		
		if (l.isDuplicate(title)) {
			System.out.printf("제목이 중복되었습니다.");
			return;
		}
		
		System.out.print("카테고리 > ");
		
		category = sc.nextLine();
		
		System.out.print("내용 > ");
		
		desc = sc.nextLine();
		
		
		System.out.print("마감일자 > ");
		due_date = sc.nextLine();
		
		System.out.print("장소 > ");
		place = sc.nextLine();
		
		TodoItem t = new TodoItem(title, category, desc, due_date, place);
		//list.addItem(t);
		if(l.addItem(t) > 0)
			System.out.println("추가되었습니다.");
	}

	public static void deleteItem(TodoList l) {
		

		Scanner sc = new Scanner(System.in);
		
		int[] array = null;
		
		System.out.println("[항목 삭제]\n몇 개의 항목을 삭제하시겠습니까?");
		int number = sc.nextInt();
		array = new int[number];
		
		System.out.println("삭제할 항목의 번호를 입력하시오. > ");
		for(int i = 0; i < number; i++)
			array[i] = sc.nextInt();
		
		int flag = 0;
		
		for(int i = 0; i < number; i++)
		{
			if(l.deleteItem(array[i], number) > 0)
				flag = 1;
			else	
				flag = 0;
		}
				
		if(flag == 1)
			System.out.println("삭제되었습니다.");
		else
			System.out.println("삭제에 문제가 있습니다.");
			
	}

	public static void updateItem(TodoList l) {
		
		Scanner sc = new Scanner(System.in);
		
		System.out.println("[항목 수정]");
		System.out.print("수정할 항목의 번호를 입력하시오 > ");
		int index = sc.nextInt();
		/*if (!l.isDuplicate(title)) {
			System.out.println("title doesn't exist");
			return;
		}*/
		
		sc.nextLine();
		
		System.out.print("새 제목 > ");
		String new_title = sc.nextLine().trim();
		if (l.isDuplicate(new_title)) {
			System.out.println("title can't be duplicate");
			return;
		}
		
		System.out.print("새 카테고리 > ");
		String new_category = sc.nextLine().trim();
		//sc.nextLine();

		
		//sc.nextLine();
		
		System.out.print("새 내용 > ");
		String new_desc = sc.nextLine().trim();
		//sc.nextLine();
		
		System.out.print("새 마감일자 > ");
		String new_due_date = sc.nextLine().trim();
		//sc.nextLine();
		
		System.out.print("새 장소 > ");
		String new_place = sc.nextLine().trim();
		
		TodoItem t = new TodoItem(new_title, new_category, new_desc, new_due_date, new_place);
		t.setId(index);
		if(l.updateItem(t) > 0)
			System.out.println("수정되었습니다.");
		
	}

	public static void listAll(TodoList l, String orderby, int ordering) {

		System.out.printf("[전체 목록, 총 %d개]\n", l.getCount());
		
		for(TodoItem item : l.getOrderedList(orderby, ordering))
		{
			System.out.println(item.toString());
		}
	}

	public static void listAll(TodoList l)
	{
		System.out.printf("[전체 목록, 총 %d개]\n", l.getCount());
		for(TodoItem item : l.getList())
		{
			System.out.println(item.toString());
		}
	}
		
	public static void findItem(TodoList l, String keyword) {
		// TODO Auto-generated method stub
		
		int count = 0;
		for (TodoItem item : l.getList(keyword))
		{
			System.out.println(item.toString());
			count++;
		}
		
		System.out.printf("총 %d개의 항목을 찾았습니다.\n", count);
		
	}
	

	public static void findCategory(TodoList l, String keyword) {
		// TODO Auto-generated method stub
		int count = 0;
		for (TodoItem item : l.getListCategory(keyword))
		{
			System.out.println(item.toString());
			count++;
		}
		
		System.out.printf("총 %d개의 항목을 찾았습니다.\n", count);
		
	}

	public static void listCategory(TodoList l) {
		// TODO Auto-generated method stub
		int count = 0;
		for (String item : l.getCategories())
		{
			System.out.print(item + " ");
			count++;
		}
		System.out.printf("\n총 %d개의 카테고리가 등록되어 있습니다.\n", count);
	}


	public static void listAll(TodoList l, int flag)
	{
		for(TodoItem item : l.getList(flag))
		{
			System.out.println(item.toString());
		}
	}

	public static void completeItem(TodoList l, int comp1) {
		
		Scanner sc = new Scanner(System.in);
		// TODO Auto-generated method stub
		int[] checkNum = new int[comp1];
		System.out.println("체크할 목록들을 입력해주세요 > ");
		
		for(int i = 0; i < comp1; i++)
			checkNum[i] = sc.nextInt();
		
		int flag = 0;
		
		for(int i = 0; i < comp1; i++)
		{
			if(l.completeItem(checkNum[i]) > 0)
				flag = 1;
			else	
				flag = 0;
		}
		
		if(flag == 1)
			System.out.println("체크되었습니다.");
		else
			System.out.println("체크에 문제가 있습니다.");
		
	}
	
	public static void listAll_t(TodoList l, int flag)
	{
		for(TodoItem item : l.getList_t(flag))
		{
			System.out.println(item.toString());
		}
	}

	public static void teamItem(TodoList l, int team1) {
		// TODO Auto-generated method stub
		if(l.teamItem(team1) > 0)
			System.out.println("팀플 일정 체크하였습니다.");
		
	}
		
}
