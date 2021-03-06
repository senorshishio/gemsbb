package gemsbb

import grails.gorm.annotation.Entity
import grails.rest.*
import org.bson.types.ObjectId

@Entity
@Resource(readOnly = false, formats = ['json', 'xml'])
class Mapping {
    ObjectId id
    Project project
    String tool
    List<MappingDetail> map

    static embedded = ['map']

    static constraints = {
        project nullable: false
        tool nullable: false
    }

    static mapping = {
        version false
    }
}
