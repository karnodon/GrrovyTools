/**
 * Created with IntelliJ IDEA.
 * User: Frostbite
 * Date: 05.01.13
 * Time: 20:04
 */
import static groovy.io.FileType.FILES

class MP3List {
    ArrayList foundFiles;
    File root;
    PrintStream fw;
    boolean showFiles;
    int totalAlbums;
    int totalFiles;

    def walk(root, fileList) {
        ArrayList files = new ArrayList();
        ArrayList dirs = new ArrayList();
        root.eachFile() {
            if (it.name.endsWith('.mp3') || it.name.endsWith(".ogg") || it.name.endsWith(".wma")) {
                files.add(it.getAbsolutePath())
            }
            if (it.directory) {
                dirs.add(it);
            }
        };
        if (files.size() > 0 || dirs.size() > 0) {
            fileList.add(root.getAbsolutePath());
            fileList.add(files);
            totalFiles += files.size();
            if (dirs.isEmpty()) {
                totalAlbums++;
            }
            for (d in dirs) {
                ArrayList subList = new ArrayList();
                fileList.add(subList);
                walk(d, subList);
            }
        }
    }

    def print(tgt, htmlFile) {
        if (!tgt.isEmpty()) {
            htmlFile.print(("<li>" + tgt[0] + "</li><ul>"))
            if (showFiles)
                try {
                    def files = tgt[1]
                    for (f in files)
                        htmlFile.print("<li>" + f + "</li>")
                }
                catch (Exception e) {}
            try {
                for (int i = 2; i < tgt.size(); i++) {
                    print(tgt[i], htmlFile)
                }
            }
            catch (Exception e) {}
            htmlFile.print("</ul>")
        }
    }

    public MP3List(boolean showFiles, String root, String htmlPath) {
        this.showFiles = showFiles;
        totalAlbums = 0;
        totalFiles = 0;
        foundFiles = new ArrayList()
        long start = System.currentTimeMillis()
        try {
            fw = new PrintStream(new FileOutputStream(htmlPath));
            walk(new File(root), foundFiles)
            fw.print("<html><body><ul>")
            print(foundFiles, fw)
            fw.print("</ul><ul><li>всего альбомов: " + totalAlbums + "</li>")
            fw.print("<li>всего записей: " +totalFiles + "</li></ul>")
            fw.print("</body></html>")
            start =  (System.currentTimeMillis() - start)/1000
            println "Выполнено за " + start + "с"
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            println "Usage: java MP3List [-f] <root> <target>"
        } else {
            int argIdx = 0
            if (args[0] == "-f")
                argIdx = 1
            new MP3List(args[0] == "-f", args[argIdx], args[argIdx + 1])
        }
    }
}
