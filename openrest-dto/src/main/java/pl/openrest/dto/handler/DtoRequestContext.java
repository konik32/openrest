package pl.openrest.dto.handler;

public class DtoRequestContext {

    private Object dto;
    private Object entity;
    private boolean isNew  = true;
    public DtoRequestContext(){
        
    }
    public DtoRequestContext(Object dto, Object entity, boolean isNew) {
        this.dto = dto;
        this.entity = entity;
        this.isNew = isNew;
    }

    public Object getDto() {
        return dto;
    }

    public void setDto(Object dto) {
        this.dto = dto;
    }

    public Object getEntity() {
        return entity;
    }

    public void setEntity(Object entity) {
        this.entity = entity;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }
}
