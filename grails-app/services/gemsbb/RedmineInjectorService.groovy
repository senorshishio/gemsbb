package gemsbb

import grails.transaction.Transactional
import grails.plugins.rest.client.RestBuilder
import org.grails.web.json.JSONObject
import org.springframework.http.HttpStatus

@Transactional
class RedmineInjectorService {
    RestBuilder restClient = new RestBuilder()

    private String getProjectId(Integer id, String name) {
        def resp = restClient.get("http://localhost:8081/projects/search?externalKey=${id}&tool=Redmine")
        JSONObject result = resp.json

        if(result.size() == 1) {
            return result.id
        }
        else {
            def rpost = restClient.post('http://localhost:8081/projects') {
                contentType "application/json"
                json {
                    name = name
                }
            }
            return rpost.json.id
        }
    }

    private String getPlanId(Integer id) {
        def resp = restClient.get("http://localhost:8081/plans/search?externalKey=${id}&tool=Redmine")
        JSONObject result = resp.json

        if(result.size() == 1) {
            return result.id
        }
    }

    private String getMemberId(Integer id) {
        def resp = restClient.get("http://localhost:8081/members/search?externalKey=${id}&tool=Redmine")
        JSONObject result = resp.json

        if(result.size() == 1) {
            return result.id
        }
        else {
            def apiKey = 'baa9da1d47247ea95bedc425027e7bb30df8f883'
            def user = restClient.get("http://localhost:8081/users.json?project_id=3&key=${apiKey}").json.user
            def rpost = restClient.post('http://localhost:8081/members') {
                contentType "application/json"
                json {
                    name = "${user.firstname} ${user.lastname}"
                    email = user.mail
                }
            }

            return rpost.json.id
        }
    }

    private def getTask(JSONObject issue) {
        def responsible = null
        if(issue.assigned_to != null) {
            getMemberId(issue.assigned_to.id.toInteger())
        }

        [
            name: "Task 1",
            startDate: Date.parse('yyyy-MM-dd', issue.start_date),
            dueDate: Date.parse('yyyy-MM-dd', issue.due_date),
            status: issue.status.name,
            responsible: responsible,
            contributors: ["57b135d78acec62754906455"]
        ]
    }


    def injectProjectPlan(Integer externalProjectId) {
        // 1. Obtener proyecto. Si no está en bb, crear. (esto debiera ser en otro método)
        //  1.1 Obtener por id.
        // 2. Para cada tarea del plan:
        //  2.1. Obtener responsable. Si no existe en bb, crear.
        //  2.2. Agregar issues a la lista de tareas.

        //def resp = restClient.get("http://10.0.2.2:3000/issues.json?project_id=3")
        def resp = restClient.get("http://localhost:8081/issues.json?project_id=${externalProjectId}")
        JSONObject result = resp.json
        if(result.issues.size() > 0) {
            def firstIssue = result.issues[0]

            def projectId = getProjectId(firstIssue.project.id, firstIssue.project.name)

            def taskList = []

            result.issues.each {
                taskList.add(getTask(it))
            }

            def planId = getPlanId(externalProjectId)
            def responsePlan
            if(planId == null) {
                responsePlan = restClient.post("http://localhost:8081/plans") {
                    contentType "application/json"
                    json {
                        externalKey = externalProjectId
                        tool = 'Redmine'
                        project = projectId
                        tasks = taskList
                    }
                }
            }
            else {
                responsePlan = restClient.put("http://localhost:8081/plans") {
                    contentType "application/json"
                    json {
                        id = planId
                        externalKey = externalProjectId
                        tool = 'Redmine'
                        project = projectId
                        tasks = taskList
                    }
                }
            }

            if (responsePlan.getStatusCode() != HttpStatus.OK) {
                throw new Exception("Error al guardar el registro del plan. HttpStatusCode: ${responsePlan.getStatusCode()}")
            }
        }
    }
}
