package example.edu.fudan.example.nlp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.fnlp.app.keyword.AbstractExtractor;
import org.fnlp.app.keyword.WordExtract;

import edu.fudan.nlp.cn.tag.CWSTagger;
import edu.fudan.nlp.corpus.StopWords;
import edu.fudan.util.exception.LoadModelException;

class AnalysisPdf {
	public static String getText(String file) {
		String s = "";
		String pdffile = file;
		PDDocument pdfdoc = null;
		try {
			pdfdoc = PDDocument.load(pdffile);
			PDFTextStripper stripper = new PDFTextStripper();
			s = stripper.getText(pdfdoc);
			pdfdoc.close();
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
		return s;
	}
}
public class AnalysisNewData {
	public static void main(String args[]) throws LoadModelException, IOException{
		
		StopWords sw= new StopWords("models/stopwords");
		CWSTagger seg = new CWSTagger("models/seg.m");
		AbstractExtractor key = new WordExtract(seg,sw);
		BufferedWriter extractout;

		extractout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data/weather_keyword_ext_2004.txt")));

		AnalysisPdf ap = new AnalysisPdf();
		
		File root =  new File("data/data2004");
		for (File fld:root.listFiles()){
			for (File sld:fld.listFiles()){
				try{
				System.out.println(sld);
				String filename = sld.getName();
				System.out.println(filename);
				String text = ap.getText(sld.toString());
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data/weatherext2004/"+filename+".txt")));
				bw.write(text);
				bw.close();
				
				key.extract(text,50,true);
				extractout.write(filename+"\t"+key.extract(text,50,true)+"\n");
				}
				catch(Exception e){
					e.printStackTrace();
				}
//				System.out.println(text);
			}
		}
		
		root =  new File("data/data2004ext");
		for (File fld:root.listFiles()){
			for (File sld:fld.listFiles()){
				try{
				System.out.println(sld);
				String filename = sld.getName();
				System.out.println(filename);
				String text = ap.getText(sld.toString());
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data/weatherext2004/"+filename.replace("-","")+".txt")));
				bw.write(text);
				bw.close();
				
				key.extract(text,50,true);
				extractout.write(filename.replace("-","")+"\t"+key.extract(text,50,true)+"\n");
				}
				catch(Exception e){
					e.printStackTrace();
				}
//				System.out.println(text);
			}
		}
		
		extractout.close();
		
		
		
	}
}

