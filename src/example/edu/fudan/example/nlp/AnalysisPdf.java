package example.edu.fudan.example.nlp;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.sql.Statement;

import org.fnlp.app.keyword.AbstractExtractor;
import org.fnlp.app.keyword.WordExtract;

import edu.fudan.nlp.cn.tag.CWSTagger;
import edu.fudan.nlp.corpus.StopWords;
import edu.fudan.util.exception.LoadModelException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.*;

class Document {
	public  Document(String time,String title,String text){
		this.time = time;
		this.title = title;
		this.text = text;
	}
	public String time;
	public String title;
	public String text;
	public String keywords;

	public static void toTextFile(String doc, String filename) throws Exception {
		String pdffile = doc;
		PDDocument pdfdoc = PDDocument.load(doc);
		try {
			pdfdoc = PDDocument.load(pdffile);
			PDFTextStripper stripper = new PDFTextStripper();
			PrintWriter pw = new PrintWriter(new FileWriter(filename));
			stripper.writeText(pdfdoc, pw);
			pdfdoc.close();
			pw.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (pdfdoc != null) {
					pdfdoc.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static Connection getConnection(String dbname) {
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/" + dbname
				+ "?user=root&password=luocheng";
		Connection conn = null;
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

	public static Statement createStatement(Connection conn) {
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return stmt;
	}

	public static ResultSet executeQuery(Statement stmt, String sql) {
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}

	public static void main(String[] args) throws SQLException, LoadModelException, IOException {
		StopWords sw= new StopWords("models/stopwords");
		CWSTagger seg = new CWSTagger("models/seg.m");
		AbstractExtractor key = new WordExtract(seg,sw);
		
		Connection conn_content = getConnection("pw_tmsgs");
		Connection conn_meta = getConnection("pw_threads");

		Statement stmt_content = createStatement(conn_content);
		Statement stmt_meta = createStatement(conn_meta);
		BufferedWriter extractout;

		extractout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data/weather_keyword.txt")));

		for (int i = 1; i <= 1373928; i++) {

			ResultSet rs_content = executeQuery(stmt_content,
					"SELECT * FROM pw_tmsgs.pw_tmsgs WHERE tid = " + i + ";");
			ResultSet rs_meta = executeQuery(stmt_meta,
					"SELECT * FROM pw_threads.pw_threads WHERE tid = " + i + ";");

			
			try {
				while (rs_content.next() && rs_meta.next()) {
					Date date = new Date(rs_meta.getLong(12)*1000);
					String title = rs_meta.getString(7);
					String dates = new String((date.getYear()+1900)+"_"+date.getMonth()+"_"+date.getDate());
					
					String text = rs_content.getString(12);
					
					if (text.contains("气候变化")){
						BufferedWriter bw;
						try {
//							bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data/weather/"+dates+"_"+title+".txt")));
							extractout.write(dates+"_"+title+"\t"+key.extract(text,20,true)+"\n");
//							bw.write(text);
//							bw.close();
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println(i);
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		extractout.close();

	}

}