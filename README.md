<h1>Web Server JAVA project</h1>
<h3>To test the server:</h3>
<ol>
  <li>Fork the project and pull it locally.</li>
  <li>Change the <b>src/main/resources/application.properties</b> as per your requirement and availablity.</li>
  <li>Run the project and use below curl commands for testing</li>
  <ul>
    <li>GET: curl -i -X GET http://localhost:8080/index.html</li>
    <li>GET: curl -i -X GET http://localhost:8080/index1.html</li>
    <li>POST: curl --header "Content-Type: application/json" --request POST --data "{\"Language\":\"JAVA\"}" http://localhost:8080/index.html</li>
  </ul>
    
</ol>
<h5><b>PS: Project is under inital stage so expect errors and limited functionality!!</b></h5>
