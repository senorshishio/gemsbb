package gemsbb

import grails.gorm.annotation.Entity
import grails.rest.*
import org.bson.types.ObjectId

@Entity
@Resource(readOnly = false, formats = ['json', 'xml'])
class TaskTrace {
    ObjectId taskTraceId
    String name
    String status
    List<TraceDetail> traceDetails

    static embedded = ['traceDetails']

    static constraints = {
        taskTraceId nullable: false
        name nullable: false
        status nullable: false
    }

    static mapping = {
        version false
    }
}
