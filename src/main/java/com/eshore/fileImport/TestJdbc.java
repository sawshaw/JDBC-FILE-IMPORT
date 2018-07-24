package com.eshore.fileImport;

public class TestJdbc {
	public static void main(String[] args) {
		Dbutils util=new Dbutils();
		String sql="insert into GDT_LIST_RT values('18925121155','16820456','20180320','080318','81','2','1.00','','668','668',now(),'P168_668_20180320080000.txt')";
		util.update(sql);
		System.out.println("success");
	}

}
