package idea.verlif.justdata.route;

import idea.verlif.justdata.item.Item;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

/**
 * @author Verlif
 */
public class Router {

    private final String label;

    private final HashMap<String, Item> getMap;
    private final HashMap<String, Item> postMap;
    private final HashMap<String, Item> deleteMap;
    private final HashMap<String, Item> putMap;

    public Router(String label) {
        this.label = label;

        this.getMap = new HashMap<>();
        this.postMap = new HashMap<>();
        this.deleteMap = new HashMap<>();
        this.putMap = new HashMap<>();
    }

    public void addItem(Item item) {
        switch (item.getMethod()) {
            case "GET":
                getMap.put(item.getApi(), item);
                break;
            case "PUT":
                putMap.put(item.getApi(), item);
                break;
            case "DELETE":
                deleteMap.put(item.getApi(), item);
                break;
            default:
                postMap.put(item.getApi(), item);
        }
    }

    public Item get(String api) {
        return getMap.get(api);
    }

    public Set<String> getSet() {
        return getMap.keySet();
    }

    public Item post(String api) {
        return postMap.get(api);
    }

    public Set<String> postSet() {
        return postMap.keySet();
    }

    public Item put(String api) {
        return putMap.get(api);
    }

    public Set<String> putSet() {
        return putMap.keySet();
    }

    public Item delete(String api) {
        return deleteMap.get(api);
    }

    public Set<String> deleteSet() {
        return deleteMap.keySet();
    }

    public String getLabel() {
        return label;
    }

    public Info getInfo() {
        return new Info();
    }

    public final class Info implements Serializable {

        private final Set<String> get;
        private final Set<String> post;
        private final Set<String> put;
        private final Set<String> delete;

        public Info() {
            get = getSet();
            post = postSet();
            put = putSet();
            delete = deleteSet();
        }

        public Set<String> getGet() {
            return get;
        }

        public Set<String> getPost() {
            return post;
        }

        public Set<String> getPut() {
            return put;
        }

        public Set<String> getDelete() {
            return delete;
        }
    }
}
