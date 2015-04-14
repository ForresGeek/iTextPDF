package com.elende.iTextPDF;


import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import com.itextpdf.text.pdf.codec.TiffImage;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


public class TestHarness {

	

	public final static Logger LOGGER =LogManager.getLogger("com.restartconsulting.iTextPDF");
	
	
	/* Usage: tiffToPDF  inputfile   outputfile       -    use full file / pathnames */
	

  @SuppressWarnings("deprecation")
public static void main(String[] args) {
 
	  
	  int pages = 0;
	  String tiffName = "";
	  String pdfName = "";
	 	  
	
	  LOGGER.trace("ArgCount "+args.length);
	  
	  if (args.length < 2) {		  
		  LOGGER.trace("Quitting... not enough args ");
		  System.out.println("Usage: Tiff2Pdf infilename outfilename  ");
		  System.exit(1);
      }
    
	  
    
    
     tiffName = args[0];
     pdfName = args[1];
 
    LOGGER.debug(String.format("Files: %s    %s",tiffName,pdfName));
    
    
     if(! new File(tiffName).exists()){
    	 LOGGER.warn("Can't open input file:"+tiffName);
      System.exit(1);
     }
    	 
    
    
       //Create new output PDF
       Document document = new Document(PageSize.A4, 0, 0, 0, 0);
       
       try {
    	   
          PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfName));
          
       
          
          document.open();
          
          PdfContentByte cb = writer.getDirectContent();
      
          RandomAccessFileOrArray ra = null;
          int comps = 0;
          try {
             ra = new RandomAccessFileOrArray(tiffName);
             comps = TiffImage.getNumberOfPages(ra);
             LOGGER.debug(String.format("input file has %d pages",comps));
          }
          catch (Throwable e) {
             LOGGER.error("Exception in " + tiffName , e);
             System.exit(0);
          }
          
          
          
          System.out.println("Processing: " + tiffName);
          		for (int c = 0; c < comps; ++c) {
          			try {
          				Image img = TiffImage.getTiffImage(ra, c + 1);
          				if (img != null) {
                	
          					LOGGER.info(String.format  ("page %d    img.getDpiX %d    img.getDpiY %d", c+1,	img.getDpiX(),img.getDpiY()));
          					img.scalePercent(6200f / img.getDpiX(), 6200f / img.getDpiY());
          					document.setPageSize(new Rectangle(img.getScaledWidth(), img.getScaledHeight()));
          					img.setAbsolutePosition(0, 0);
          					cb.addImage(img);
          					document.newPage();
          					++pages;
          				}
          			}
          			catch (Throwable e) {
          				LOGGER.error("Exception " + tiffName + " page "+ (c + 1) ,e);
          			}
          		}
          		
          ra.close();
          document.close();
       } 
       catch (Throwable e) {	 
        	  LOGGER.error("Error processing TIF",e);
        }
  
          LOGGER.trace("Done");
          
          System.exit(1);
          
  }
  
}