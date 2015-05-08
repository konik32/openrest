package orest.model;

import static com.mysema.query.types.PathMetadataFactory.forVariable;

import javax.annotation.Generated;

import com.mysema.query.types.Path;
import com.mysema.query.types.PathMetadata;
import com.mysema.query.types.path.EntityPathBase;
import com.mysema.query.types.path.NumberPath;
import com.mysema.query.types.path.PathInits;
import com.mysema.query.types.path.SetPath;
import com.mysema.query.types.path.StringPath;

/**
 * QProduct is a Querydsl query type for Product
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QProduct extends EntityPathBase<Product> {

	private static final long serialVersionUID = -1335718024L;

	private static final PathInits INITS = PathInits.DIRECT2;

	public static final QProduct product = new QProduct("product");

	public final org.springframework.data.jpa.domain.QAbstractPersistable _super = new org.springframework.data.jpa.domain.QAbstractPersistable(this);

	public final StringPath description = createString("description");

	public final NumberPath<Long> id = createNumber("id", Long.class);

	public final StringPath name = createString("name");

	public final NumberPath<Integer> productionYear = createNumber("productionYear", Integer.class);

	public final QUser user;
	
    public final SetPath<Tag, QTag> tags = this.<Tag, QTag>createSet("tags", Tag.class, QTag.class, PathInits.DIRECT2);

	public QProduct(String variable) {
		this(Product.class, forVariable(variable), INITS);
	}

	public QProduct(Path<? extends Product> path) {
		this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
	}

	public QProduct(PathMetadata<?> metadata) {
		this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
	}

	public QProduct(PathMetadata<?> metadata, PathInits inits) {
		this(Product.class, metadata, inits);
	}

	public QProduct(Class<? extends Product> type, PathMetadata<?> metadata, PathInits inits) {
		super(type, metadata, inits);
		this.user = inits.isInitialized("user") ? new QUser(forProperty("user"), inits.get("user")) : null;
	}

}
