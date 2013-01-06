/**
 * Created with IntelliJ IDEA.
 * User: Frostbite
 * Date: 05.01.13
 * Time: 20:04
 * To change this template use File | Settings | File Templates.
 */
import static groovy.io.FileType.FILES
class MP3List {
    TreeMap foundFiles;
    File root;
    PrintStream fw;
    boolean showFiles;
    int totalAlbums;

    private boolean walk(File directory, TreeMap store) {
        boolean foundAnything = false;
        if (directory.isDirectory()) {
            ArrayList files = new ArrayList();
            ArrayList<File> dirs = new ArrayList<File>();
            directory.eachFile() {
                if(it.name.endsWith('.mp3')  || it.name.endsWith(".ogg") || it.name.endsWith(".wma")) {
                    files.add(it)
                }
                if (it.directory) {
                    dirs.add(it);
                }
            };
            ArrayList data = new ArrayList(2);
            store.put(directory, data);
            if (files != null) {
                if (files.size() > 0) {
                    data.add(files);
                    totalAlbums++;
                    foundAnything = true;
                }
                if (!dirs.isEmpty()) {
                    TreeMap<File, ArrayList> sub = new TreeMap<File,ArrayList>();
                    data.add(sub);
                    for (dir in dirs) {
                        if (!walk(dir, sub)) {
                            sub.remove(dir);
                        }
                        else {
                            foundAnything = true;
                        }
                    }
                    sub.sort() {a,b -> a.key.getAbsolutePath() <=> b.key.getAbsolutePath()};
                }
            }
            else {
                println(directory.getAbsolutePath());
            }
        }
        return foundAnything;
    }

    private void print(TreeMap result) {
        fw.println("<UL>");
        for (Iterator iterator = result.keySet().iterator(); iterator.hasNext();) {
            File key = (File) iterator.next();
            ArrayList o = (ArrayList) result.get(key);
            fw.println("<LI>" + key + "</LI>");

            for (int i = 0; i < o.size(); i++) {
                Object o1 = o.get(i);
                if (o1 instanceof ArrayList && showFiles) {
                    fw.print("<UL>");
                    for (path in o1) {
                        fw.println("<LI>" + path + "</LI>");
                    }
                    fw.println("</UL>");
                } else if (o1 instanceof TreeMap) {
                    print((TreeMap) o1);
                }
            }
        }
        fw.println("</UL>");
    }

    public MP3List(boolean showFiles) {
        this.showFiles = showFiles;
        totalAlbums = 0;
        foundFiles = new TreeMap(new Comparator() {
            public int compare(Object o1, Object o2) {
                File f1 = (File) o1;
                File f2 = (File) o2;
                return f1.getAbsolutePath().compareTo(f2.getAbsolutePath());  //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        try {
            fw = new PrintStream(new FileOutputStream("c:/Users/Frostbite/Documents/result.html"));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static void main(String[] args) {
        boolean b = false;
        if (args.length > 1)
            b = "Y".equalsIgnoreCase(args[1]);
        MP3List l = new MP3List(b);
        l.walk(new File(args[0]), l.foundFiles);
        l.fw.print("<HTML><BODY>");
        l.fw.print("<H1>Total albums: " + l.totalAlbums + "</H1>");
        l.print(l.foundFiles);
        l.fw.print("</BODY></HTML>");
        l.fw.flush();
    }
}
