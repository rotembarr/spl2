package bgu.spl.mics.application;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;

import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Data;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;
import bgu.spl.mics.application.services.CPUService;
import bgu.spl.mics.application.services.ConferenceService;
import bgu.spl.mics.application.services.GPUService;
import bgu.spl.mics.application.services.StudentService;
import bgu.spl.mics.application.services.TimeService;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {

    // All System services.
    private TimeService timeService = null;
    private List<StudentService> studentServices = null;
    private List<GPUService> gpuServices = null;
    private List<CPUService> cpuServices = null;
    private List<ConferenceService> conferenceServices = null;
    
    // All system threads
    private Thread timeThread = null;
    private List<Thread> studentThreads = null;
    private List<Thread> gpuThreads = null;
    private List<Thread> cpuThreads = null;
    private List<Thread> conferenceThreads = null;

    // Uses as a struct.
    private class JsonStudentInformation {
        private String name;
        private String department;
        private Student.Degree status;    
        private List<Data> models;
    }
    private class JsonConferenceInformation {
        private String name;
        private int date;
    }

    private class JsonInformation {
        public List<JsonStudentInformation> Students;        
        public List<GPU.Type> GPUS;
        public List<Integer> CPUS;
        public List<JsonConferenceInformation> Conferences;
        public int TickTime;
        public int Duration;
    }

    private class JsonStatistics {
        @Expose(serialize = true, deserialize = true)
        public List<Student> students;        
        @Expose(serialize = true, deserialize = true)
        public List<ConfrenceInformation> conferences;
        @Expose(serialize = true, deserialize = true)
        public int gpusTimeUsed;
        @Expose(serialize = true, deserialize = true)
        public int gpusTimePass;
        @Expose(serialize = true, deserialize = true)
        public int cpusTimeUsed;
        @Expose(serialize = true, deserialize = true)
        public int cpusTimePass;
        @Expose(serialize = true, deserialize = true)
        public int batchesProcessed;
    }


    public CRMSRunner(String inputJsonFilePath) {
        JsonInformation jsonInformation = null;
        Gson gson = new Gson();

        // Parse json into JsonInformation.
        try (Reader reader = new FileReader(inputJsonFilePath)) {
            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(reader);
            jsonInformation = gson.fromJson(jsonElement.toString(), JsonInformation.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Extract parsing.
        this.studentServices = new ArrayList<StudentService>();
        for (int i = 0; i < jsonInformation.Students.size(); i++) {
            JsonStudentInformation si = jsonInformation.Students.get(i);
            StudentService sts = new StudentService(si.name, si.department, si.status); 
            for (int j = 0; j < si.models.size(); j++) {
                sts.addModelToTrain(new Model(si.models.get(j).getName(), si.models.get(j), sts.getStudent()));
            }
            this.studentServices.add(sts);
        }
        this.gpuServices = new ArrayList<GPUService>();
        for (int i = 0; i < jsonInformation.GPUS.size(); i++) {
            this.gpuServices.add(new GPUService("GPU-"+Integer.toString(i), jsonInformation.GPUS.get(i)));
        }
        this.cpuServices = new ArrayList<CPUService>();
        for (int i = 0; i < jsonInformation.CPUS.size(); i++) {
            this.cpuServices.add(new CPUService("CPU-"+Integer.toString(i), jsonInformation.CPUS.get(i)));
        }
        this.conferenceServices = new ArrayList<ConferenceService>();
        for (int i = 0; i < jsonInformation.Conferences.size(); i++) {
            this.conferenceServices.add(new ConferenceService(jsonInformation.Conferences.get(i).name, jsonInformation.Conferences.get(i).date));
        }
        this.timeService = new TimeService("Time Service", jsonInformation.TickTime, jsonInformation.Duration);
    }

    /**
     * Start all threads
     */
    private void startAllThreads() {
        this.studentThreads = new ArrayList<Thread>();
        for (int i = 0; i < studentServices.size(); i++) {
            Thread thread = new Thread(this.studentServices.get(i));
            thread.start();
            this.studentThreads.add(thread);
        }

        this.gpuThreads = new ArrayList<Thread>();
        for (int i = 0; i < gpuServices.size(); i++) {
            Thread thread = new Thread(this.gpuServices.get(i));
            thread.start();
            this.gpuThreads.add(thread);
        }


        this.cpuThreads = new ArrayList<Thread>();
        for (int i = 0; i < gpuServices.size(); i++) {
            Thread thread = new Thread(this.cpuServices.get(i));
            thread.start();
            this.cpuThreads.add(thread);
        }

        this.conferenceThreads = new ArrayList<Thread>();
        for (int i = 0; i < conferenceServices.size(); i++) {
            Thread thread = new Thread(this.conferenceServices.get(i));
            thread.start();
            this.conferenceThreads.add(thread);
        }

        this.timeThread = new Thread(timeService);
        this.timeThread.start();

    }

    /**
     * Interupt all threads
     */
    private void interuptAllThreads() {
        
        for (int i = 0; i < studentServices.size(); i++) {
            this.studentThreads.get(i).interrupt();
        }

        for (int i = 0; i < gpuServices.size(); i++) {
            this.gpuThreads.get(i).interrupt();
        }    

        for (int i = 0; i < gpuServices.size(); i++) {
            this.cpuThreads.get(i).interrupt();
        }

        for (int i = 0; i < conferenceServices.size(); i++) {
            this.conferenceThreads.get(i).interrupt();
        }
    }

    /**
     * Join all threads
     */
    private void joinAllThreads() {
        try {
            for (int i = 0; i < studentServices.size(); i++) {
                this.studentThreads.get(i).join();
            }

            for (int i = 0; i < gpuServices.size(); i++) {
                this.gpuThreads.get(i).join();
            }    
                
            for (int i = 0; i < gpuServices.size(); i++) {
                this.cpuThreads.get(i).join();
            }
            
            for (int i = 0; i < conferenceServices.size(); i++) {
                this.conferenceThreads.get(i).join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    

    /**
     * Output result
     * 
     */
    public void log(String outputJsonFilePath) {
        JsonStatistics jsonStatistics = new JsonStatistics();

        jsonStatistics.students = new LinkedList<Student>();
        for (int i = 0; i < this.studentServices.size(); i++) {
            jsonStatistics.students.add(this.studentServices.get(i).getStudent());
        }
        jsonStatistics.conferences = new LinkedList<ConfrenceInformation>();
        for (int i = 0; i < this.conferenceServices.size(); i++) {
            jsonStatistics.conferences.add(this.conferenceServices.get(i).getInformation());
        }
        for (int i = 0; i < this.gpuServices.size(); i++) {
            jsonStatistics.gpusTimeUsed += this.gpuServices.get(i).getGpu().getNumOfTimeUsed();
            jsonStatistics.gpusTimePass += this.gpuServices.get(i).getGpu().getNumOfTimePass();
        }            
        for (int i = 0; i < this.cpuServices.size(); i++) {
            jsonStatistics.cpusTimeUsed += this.cpuServices.get(i).getCpu().getNumOfTimeUsed();
            jsonStatistics.cpusTimePass += this.cpuServices.get(i).getCpu().getNumOfTimePass();
            jsonStatistics.batchesProcessed += this.cpuServices.get(i).getCpu().getNumOfProcessedBatches();
        } 

        
        try (FileWriter writer = new FileWriter(outputJsonFilePath)) {
            
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            // Gson gson = new Gson();
            gson.toJson(jsonStatistics, writer);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * Run
     */
    public void run() {

        this.startAllThreads();

        // End of running.
        try {
            timeThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.interuptAllThreads();
        this.joinAllThreads();

    }

    public static void main(String[] args) {

        // if (args.length != 2) {
        //     System.exit(1);
        // }
        // CRMSRunner crmsRunner = new CRMSRunner(args[0]);

        CRMSRunner crmsRunner = new CRMSRunner("/home/rotem/projects/spl2/example_input.json");
        crmsRunner.run();
    }
}
