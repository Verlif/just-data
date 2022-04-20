package idea.verlif.justdata.router;

import idea.verlif.justdata.base.constant.MethodConstant;
import idea.verlif.justdata.item.Item;
import idea.verlif.parser.vars.VarsContext;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.*;

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
            case MethodConstant.GET:
                getMap.put(item.getApi(), item);
                break;
            case MethodConstant.PUT:
                putMap.put(item.getApi(), item);
                break;
            case MethodConstant.DELETE:
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

    public RouterInfo getInfo() {
        return new RouterInfo();
    }

    @Schema(name = "Router信息")
    public final class RouterInfo implements Serializable {

        @Schema(name = "Get方法API列表")
        private final List<ApiInfo> get;
        @Schema(name = "Post方法API列表")
        private final List<ApiInfo> post;
        @Schema(name = "Put方法API列表")
        private final List<ApiInfo> put;
        @Schema(name = "Delelte方法API列表")
        private final List<ApiInfo> delete;

        public RouterInfo() {
            get = new ArrayList<>();
            for (Item item : getMap.values()) {
                get.add(new ApiInfo(item));
            }
            post = new ArrayList<>();
            for (Item item : postMap.values()) {
                post.add(new ApiInfo(item));
            }
            put = new ArrayList<>();
            for (Item item : putMap.values()) {
                put.add(new ApiInfo(item));
            }
            delete = new ArrayList<>();
            for (Item item : deleteMap.values()) {
                delete.add(new ApiInfo(item));
            }
        }

        public List<ApiInfo> getGet() {
            return get;
        }

        public List<ApiInfo> getPost() {
            return post;
        }

        public List<ApiInfo> getPut() {
            return put;
        }

        public List<ApiInfo> getDelete() {
            return delete;
        }
    }

    @Schema(name = "操作项API信息")
    private static final class ApiInfo {

        @Schema(name = "API")
        private final String api;

        @Schema(name = "API名称")
        private final String name;

        @Schema(name = "API方法")
        private final String method;

        @Schema(name = "API访问权限")
        private final String permission;

        @Schema(name = "请求路径参数")
        private final Set<Param> paramSet;

        @Schema(name = "请求Body参数")
        private final Set<Param> bodySet;

        public ApiInfo(Item item) {
            this.api = item.getApi();
            this.name = item.getName();
            this.method = item.getMethod();
            this.permission = item.getPermission();

            this.paramSet = parserParam(item.getSql());
            this.bodySet = parserBody(item.getSql());
        }

        private Set<Param> parserParam(String sql) {
            Set<Param> set = new HashSet<>();
            VarsContext context = new VarsContext(sql);
            context.setAreaTag("#{", "}");
            context.build((i, s, s1) -> {
                String[] ss = s1.split(":", 2);
                if (ss.length > 1) {
                    set.add(new Param(ss[0], ss[1]));
                } else {
                    set.add(new Param(ss[0]));
                }
                return s;
            });
            return set;
        }

        private Set<Param> parserBody(String sql) {
            Set<Param> set = new HashSet<>();
            VarsContext context = new VarsContext(sql);
            context.setAreaTag("@{", "}");
            context.build((i, s, s1) -> {
                String[] ss = s1.split(":", 2);
                if (ss.length > 1) {
                    set.add(new Param(ss[0], ss[1]));
                } else {
                    set.add(new Param(ss[0]));
                }
                return s;
            });
            return set;
        }

        public String getApi() {
            return api;
        }

        public String getName() {
            return name;
        }

        public String getMethod() {
            return method;
        }

        public String getPermission() {
            return permission;
        }

        public Set<Param> getParamSet() {
            return paramSet;
        }

        public Set<Param> getBodySet() {
            return bodySet;
        }

        @Schema(name = "请求参数")
        private static final class Param {

            @Schema(name = "参数名")
            private final String name;

            @Schema(name = "参数默认值")
            private final String defaultVal;

            public Param(String name) {
                this.name = name;
                this.defaultVal = null;
            }

            public Param(String name, String defaultVal) {
                this.name = name;
                this.defaultVal = defaultVal;
            }

            public String getName() {
                return name;
            }

            public String getDefaultVal() {
                return defaultVal;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || getClass() != o.getClass()) {
                    return false;
                }
                Param param = (Param) o;
                return Objects.equals(name, param.name) && Objects.equals(defaultVal, param.defaultVal);
            }

            @Override
            public int hashCode() {
                return Objects.hash(name, defaultVal);
            }
        }
    }
}
