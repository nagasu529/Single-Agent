package agent.waterallocation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Iterator;


public class Crops extends SelectApp
{
	//Farm information
        public String cropName;
        public String farmName;
        public String farmSize;
        public String district;
        
	//Calculation factors
        public double plotSize;
	public int cropStage;
	public int cropValue;
	public int droughtSensitivity;
	public int soilType;
	// We can use ET calculated function which are provieded by FAO database.
        public double ET;
	public double cropKCoefficient;
	public int  dsValue;
	public double waterReq;
	public double cropProduciton;
	public double pricePerKg;
	public double yieldAmount;
	public double stValue;
	public double cvValue;
	public double cropEU;
	public double literPerSecHec;
	public double waterReduction;
	public double totalWaterReq;
	public List<String> list = new ArrayList<String>();
        public String[] calculationArray;
	
        ArrayList<Double> order = new ArrayList<Double>();		//order array
	ArrayList<String> calList = new ArrayList<String>();	//Crop list Data
        // New list initialization
        
        ArrayList<cropType> cropT = new ArrayList<cropType>();
        ArrayList<cropType> resultList = new ArrayList<cropType>();

        
        //additional parameters which are rules based decision thinking.
        List<Integer> ds = new ArrayList<>();
        List<Double> st = new ArrayList<>();
        List<Double> cv = new ArrayList<>();
        
        SelectApp app = new SelectApp();

        public void readText(String fileName){
            try (Stream<String> stream = Files.lines(Paths.get(fileName))){
                //stream.forEach(System.out::println);
                list = stream.collect(Collectors.toList());
            }
            catch(IOException e){
                e.printStackTrace();
            }
            String[] infoarray;
            String separator ="\\s*,";
            infoarray = list.get(0).split(separator); //split data for farmer information
            farmName = infoarray[0];
            district = infoarray[1];
            farmSize = infoarray[2];
            System.out.println("Textfile reading finished");
            System.out.println("\n");
            for(String c:list){
                System.out.println(c);
            }
            System.out.println("\n");
        }
        
        public void farmFactorValues(){
            String separator ="\\s*,";
            int listSize = list.size();
            while(listSize !=1){
			
		// Array that contain result every round.
		String[] tempArray = list.get(listSize - 1).split(separator);	//creating temp array for calculate data	
			//Collecting crop stage
		cropName = tempArray[0];
                    if (tempArray[0].equals("Pasture")){
                        if (tempArray[1].equals("Initial"))
                            cropStage = 3;
                        else if (tempArray[1].equals("Development"))
                            cropStage = 2;
                        else
                            cropStage = 1;
                    }
                    else{
                        if (tempArray[1].equals("Flowering") || tempArray.equals("Grain Filling"))
                            cropStage = 4;
                        else if (tempArray[1].equals("Germination"))
                            cropStage = 3;
                        else if (tempArray[1].equals("Development"))
                            cropStage = 2;
                        else
                            cropStage = 1;
                    }
                if (tempArray[2].equals("Low"))
                    droughtSensitivity = 1;
                else if (tempArray[2].equals("Medium"))
                    droughtSensitivity = 2;
                else
                    droughtSensitivity = 3;

                plotSize = Double.parseDouble(tempArray[3]);
                yieldAmount = Double.parseDouble(tempArray[4]);
                pricePerKg = Double.parseDouble(tempArray[5]);

                if (tempArray[6].equals("Light"))
                    soilType = 3;
                else if (tempArray[6].equals("Medium"))
                    soilType = 2;
                else
                    soilType = 1;
 
                getIrrigationTypeValue(tempArray[8]);
                KcStageValue(tempArray[0], tempArray[1], tempArray[8]);
                cropKCoefficient = KcValue;
                //System.out.println("KcStage value is: "+KcStageValue);
            
            /**
                 * 
                 * Evapotranspiration calculation process
                 * 
                */
                calcSTValue();
                calcDSValue();
                calcCVValue();
                //calcCropEU();
                calcWaterRequirement();
                totalWaterReq();
                cropType xx = new cropType(cropEU, cropName, cropStage, droughtSensitivity, dsValue, stValue, 
                        cvValue, literPerSecHec, waterReq, cropKCoefficient, waterReduction);
                //addList();
                //adding multi value list
                cropT.add(xx);

		listSize --;
            }
            /*
            Iterator itr=cropT.iterator();
            
            //traverse elements of ArrayList object  
            while(itr.hasNext()){  
            cropType st = (cropType)itr.next();
                System.out.println(st.cropEU + " " + st.cropName + " " + st.cropStage +
                        " " + st.droubhtSensitivity + " " + st.dsValue + " " + st.stValue + " " + st.cvValue +
                        " " + literPerSecHec + " " + st.waterReq + " " + st.cropCoefficient + " " + st.waterReduction);
            }
            */
            calcCropEU();
        }
        
        public void farmFactorValuesText(){
            String separator ="\\s*,";
            int listSize = list.size();
            while(listSize !=1){
			
		// Array that contain result every round.
		String[] tempArray = list.get(listSize - 1).split(separator);	//creating temp array for calculate data	
			//Collecting crop stage
		cropName = tempArray[0];
                ET = 7.0;
                    if (tempArray[0].equals("Pasture")){
                        if (tempArray[1].equals("Initial"))
                            cropStage = 3;
                        else if (tempArray[1].equals("Development"))
                            cropStage = 2;
                        else
                            cropStage = 1;
                    }
                    else{
                        if (tempArray[1].equals("Flowering") || tempArray.equals("Grain Filling"))
                            cropStage = 4;
                        else if (tempArray[1].equals("Germination"))
                            cropStage = 3;
                        else if (tempArray[1].equals("Development"))
                            cropStage = 2;
                        else
                            cropStage = 1;
                    }
                if (tempArray[2].equals("Low"))
                    droughtSensitivity = 1;
                else if (tempArray[2].equals("Medium"))
                    droughtSensitivity = 2;
                else
                    droughtSensitivity = 3;

                plotSize = Double.parseDouble(tempArray[3]);
                yieldAmount = Double.parseDouble(tempArray[4]);
                pricePerKg = Double.parseDouble(tempArray[5]);

                if (tempArray[6].equals("Light"))
                    soilType = 3;
                else if (tempArray[6].equals("Medium"))
                    soilType = 2;
                else
                    soilType = 1;
                cropKCoefficient = Double.parseDouble(tempArray[7]);
            
            /**
                 * 
                 * Evapotranporation calculation process
                 * 
                */
                calcSTValue();
                calcDSValue();
                calcCVValue();
                calcCropEU();
                calcWaterRequirement();
                totalWaterReq();
                //addList();

		listSize --;
            }
            for(String c:calList){
                System.out.println(c);
            }
        }
        
        public void addList()
	{
            order.add(cropEU);
            Collections.sort(order);
            Collections.reverse(order);
            int x = order.indexOf(cropEU);
            calList.add(x, cropEU + "  " + cropName + "  "+ cropStage + "  " + droughtSensitivity + "  "+ dsValue + "  " + stValue + 
                    "  " + cvValue + "  " + cropEU + "  " + literPerSecHec + "  " + waterReq + "  " + cropKCoefficient + "  " + waterReduction);
	}

	public void calcWaterRequirement()
	{
            double mmPerDay;
            double cumerPerDay;
	
            mmPerDay = cropKCoefficient * ET;
            cumerPerDay = mmPerDay * plotSize*10;
            literPerSecHec = ((cumerPerDay*1000)/plotSize)/(24*60*60);
            waterReq = cumerPerDay; 
	}
	
	public void totalWaterReq()
	{
            totalWaterReq = totalWaterReq + waterReq;
	}
	
	public void calcDSValue(){	
            int tempPasture = 0;
            if (cropName.equals("Pasture")){
                if (cropStage == 1)
                    tempPasture = 10;
                else if (cropStage == 2) 
                    tempPasture = 20;
                else 
                    tempPasture= 30;
                dsValue = tempPasture;
            }
            else{
                String tempdsValueSt = "";
                tempdsValueSt = cropStage + Integer.toString(droughtSensitivity);
                dsValue = Integer.parseInt(tempdsValueSt);
            }
            ds.add(dsValue);
	}
		
	public void calcSTValue(){
            stValue = soilType * plotSize;
            st.add(stValue);
	}
	
	public void calcCVValue(){
            cvValue = (plotSize*yieldAmount)*pricePerKg;
            cv.add(cvValue);
	}
	
	public void calcCropEU(){            
            //Decision-2            
            //summation of DSValue
            double sumDS = 0;
            for (int i = 0; i < ds.size(); i++) {
                sumDS += (ds.get(i));
            }
            double totalDS = (ds.size() * 33)*0.6; //60% of total Drought Sensitivity
            
            //Decision-1-prepaCiring
            Collections.sort(cv);
            double sumCV = 0;
            for (int i = 0; i < cv.size(); i++) {
                sumCV += (cv.get(i));
            }
            //temp test display
      
            if(cv.get(cv.size()-1)>=(sumCV*0.7)){                                   //First : cv.get(cv.size()-1)>=(sumCV*0.7)
                System.out.println("Choosing decision 1");
                System.out.println("First priority: Crop value");
                //System.out.println();
                
                
                Iterator itr=cropT.iterator();
                while (itr.hasNext()) {
                    cropType st = (cropType)itr.next();
                    st.cropEU = (0.4 * st.dsValue) + (0.2 * st.stValue) + (0.6 * st.cvValue);
                    order.add(st.cropEU);
                    Collections.sort(order);
                    //Collections.reverse(order);
                    /*System.out.println(st.cropEU + " " + st.cropName + " " + st.cropStage +
                        " " + st.droubhtSensitivity + " " + st.dsValue + " " + st.stValue + " " + st.cvValue +
                        " " + literPerSecHec + " " + st.waterReq + " " + st.cropCoefficient + " " + st.waterReduction);
                    */
                    int x = order.indexOf(st.cropEU);
                    cropType xx = new cropType(st.cropEU, st.cropName, st.cropStage, st.droubhtSensitivity, st.dsValue, st.stValue, 
                                    st.cvValue, st.literPerSecHec, st.waterReq, st.cropCoefficient, st.waterReduction);
                    resultList.add(x, xx);
                }
            }
            else if(sumDS > totalDS){                                               //Second : sumDS > totalDS
                System.out.println("Choosing decision 2");
                System.out.println("First priority: Drought Sensitivity");
                Iterator itr=cropT.iterator();
                while (itr.hasNext()) {
                    cropType st = (cropType)itr.next();
                    st.cropEU = (1.0 * st.dsValue) + (0 * st.stValue) + (0 * st.cvValue);
                    order.add(st.cropEU);
                    Collections.sort(order);
                    int x = order.indexOf(st.cropEU);
                    cropType xx = new cropType(st.cropEU, st.cropName, st.cropStage, st.droubhtSensitivity, st.dsValue, st.stValue, 
                                    st.cvValue, st.literPerSecHec, st.waterReq, st.cropCoefficient, st.waterReduction);
                    resultList.add(x, xx);
                }
            }
            else{
                System.out.println("Choosing decision 3");
                System.out.println("First priority: Drought Soil type");
                
                Iterator itr=cropT.iterator();
                while (itr.hasNext()) {
                    cropType st = (cropType)itr.next();
                    st.cropEU = (0.3 * st.dsValue) + (0.4 * st.stValue) + (0.3 * st.cvValue);
                    order.add(st.cropEU);
                    Collections.sort(order);
                    int x = order.indexOf(st.cropEU);
                    cropType xx = new cropType(st.cropEU, st.cropName, st.cropStage, st.droubhtSensitivity, st.dsValue, st.stValue, 
                                    st.cvValue, st.literPerSecHec, st.waterReq, st.cropCoefficient, st.waterReduction);
                    resultList.add(x, xx);
                }
            }
                
/*
            //Result calculation
            System.out.println("Water reduction result:");
            System.out.println("");
            Iterator itrR=resultList.iterator();
            while (itrR.hasNext()) {
                cropType st = (cropType)itrR.next();
                System.out.println(st.cropEU + " " + st.cropName + " " + st.cropStage +
                        " " + st.droubhtSensitivity + " " + st.dsValue + " " + st.stValue + " " + st.cvValue +
                        " " + literPerSecHec + " " + st.waterReq + " " + st.cropCoefficient + " " + st.waterReduction);
            }*/
	}
	
	//From Farmer file
	public double calcWaterReduction(double wr){
            double totalWaterReduction = totalWaterReq * wr;
            System.out.println("totalWater reduction:" + totalWaterReduction);
            double totalReduction = 0.0;
                Iterator itrR=resultList.iterator();	
                while (itrR.hasNext() && totalReduction <= totalWaterReduction){
                    cropType ct = (cropType)itrR.next();
                    if (ct.cropName.equals("Pasture")&& ct.cropStage==1) {
                        ct.waterReduction = ct.waterReq * 0.5;
                        totalReduction = totalReduction + ct.waterReduction;	
                    }
                    else if (ct.cropName.equals("pasture") && ct.cropStage==2) {
                        ct.waterReduction = ct.waterReq*0.2;
                        totalReduction = totalReduction + ct.waterReduction;
                    }
                    else if (ct.cropName.equals("Pasture") && ct.cropStage==3) {
                        ct.waterReduction = ct.waterReq*0.1;
                        totalReduction = totalReduction + ct.waterReduction;
                    }
                    else if (ct.cropStage==1) {
                        ct.waterReduction = ct.waterReq*0.5;
                        totalReduction = totalReduction + ct.waterReduction;
                    }
                    else if (ct.cropStage==2) {
                        ct.waterReduction = ct.waterReq*0.2;
                        totalReduction = totalReduction + ct.waterReduction;
                    }
                    else if (ct.cropStage==3) {
                        ct.waterReduction = ct.waterReq*0.15;
                        totalReduction = totalReduction + ct.waterReduction;
                    }
                    else{                        
                        ct.waterReduction = ct.waterReq*0.1;
                        totalReduction = totalReduction + ct.waterReduction;
                    }
                }
            
            return totalReduction;
	}
		
    class cropType{
        double cropEU;
        String cropName;
        int cropStage;
        int droubhtSensitivity;
        double dsValue;
        double stValue;
        double cvValue;
        double literPerSecHec;
        double waterReq;
        double cropCoefficient;
        double waterReduction;

        cropType(double cropEU, String cropName, int cropStage, int droubhtSensitivity, double dsValue, double stValue, double cvValue, double literPerSecHec, double waterReq, double cropCoefficient, double waterReduction) {
            this.cropEU = cropEU;
            this.cropName = cropName;
            this.cropStage = cropStage;
            this.droubhtSensitivity = droubhtSensitivity;
            this.dsValue = dsValue;
            this.stValue = stValue;
            this.cvValue = cvValue;
            this.literPerSecHec = literPerSecHec;
            this.waterReq = waterReq;
            this.cropCoefficient = cropCoefficient;
            this.waterReduction = waterReduction;
        }
        
        public double getCropEU(){
            return cropEU;
        }
        public void setCropEU(double cropEU){
            this.cropEU = cropEU;
        }
    }
}

/*calList.add(x, cropEU + "  " + cropName + "  "+ cropStage + "  " + droughtSensitivity + "  "+ dsValue + "  " + stValue + 
                    "  " + cvValue + "  " + cropEU + "  " + literPerSecHec + "  " + waterReq + "  " + cropKCoefficient + "  " + waterReduction);
*/