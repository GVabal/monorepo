TODO:
use fastexcel instead of apache poi to see if it is faster
```
<dependency>
    <groupId>org.dhatim</groupId>
    <artifactId>fastexcel-reader</artifactId>
    <version>0.17.0</version>
</dependency>
<dependency>
    <groupId>org.dhatim</groupId>
    <artifactId>fastexcel</artifactId>
    <version>0.17.0</version>
</dependency>

public class FastexcelHelper {

    public Map<Integer, List<String>> readExcel(String fileLocation) throws IOException {
        Map<Integer, List<String>> data = new HashMap<>();

        try (FileInputStream file = new FileInputStream(fileLocation); ReadableWorkbook wb = new ReadableWorkbook(file)) {
            Sheet sheet = wb.getFirstSheet();
            try (Stream<Row> rows = sheet.openStream()) {
                rows.forEach(r -> {
                    data.put(r.getRowNum(), new ArrayList<>());

                    for (Cell cell : r) {
                        data.get(r.getRowNum()).add(cell.getRawValue());
                    }
                });
            }
        }

        return data;
    }
}
```