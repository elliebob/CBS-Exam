package com.cbsexam;

import cache.UserCache;
import com.google.gson.Gson;
import controllers.UserController;
import model.User;
import utils.Encryption;
import utils.Log;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

@Path("user")
public class UserEndpoints {

  //Laves så der ikke laves en ny cache hver gang men kaldes en tidligere cache
   UserCache userCache = new UserCache();

  /**
   * @param idUser
   * @return Responses
   */
  @GET
  @Path("/{idUser}")
  public Response getUser(@PathParam("idUser") int idUser){

    // Use the ID to get the user from the controller.
    User user = UserController.getUser(idUser);

    // TODO: Add Encryption to JSON - FIXED
    // Convert the user object to json in order to return the object
    String json = new Gson().toJson(user);
    json = Encryption.encryptDecryptXOR(json);

    // Return the user with the status code 200
    // TODO: What should happen if something breaks down? - fixed
    if (user != null){
    return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
  } else {
    return Response.status(400).entity("User not found").build();
    }
  }

  /** @return Responses */
  @GET
  @Path("/")
  public Response getUsers(){

    // Write to log that we are here
    Log.writeLog(this.getClass().getName(), this, "Get all users", 0);

    // Get a list of users
    ArrayList<User> users = userCache.getUsers(false);

    // TODO: Add Encryption to JSON - FIXED
    // Transfer users to json in order to return it to the user
    String json = new Gson().toJson(users);
    json = Encryption.encryptDecryptXOR(json);

    // Return the users with the status code 200
    return Response.status(200).type(MediaType.APPLICATION_JSON).entity(json).build();
  }

  @POST
  @Path("/")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createUser(String body){


    // Read the json from body and transfer it to a user class
User newUser = new Gson().fromJson(body, User.class);

    // Use the controller to add the user
User createUser = UserController.createUser(newUser);

    // Get the user back with the added ID and return it to the user
String json = new Gson().toJson(createUser);

    // Return the data to the user
if (createUser != null) {
  // Return a response with status 200 and JSON as type
  return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
} else {
  return Response.status(400).entity("Could not create user").build();
}
  }


  // TODO: Make the system able to login users and assign them a token to use throughout the system. - fixed
  @POST
  @Path("/login")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response loginUser(String UserBody) {

    //UserBody from json and put into user class
    User user = new Gson().fromJson(UserBody, User.class);

    //gets the user with the id
    String token = UserController.loginUser(user);

   //Returns data to the users
    if (token != ""){
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity("Your token" + token).build(); }
    else {
      return Response.status(400).entity("Could not login").build();
    }

  }

  // TODO: Make the system able to delete users - fixed
  @DELETE
  @Path("/{userId}/{token}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response deleteUser(@PathParam("userId") int userId, @PathParam("token") String token) {

      Boolean deleted = UserController.deleteUser(token);

      if(deleted){

          // Return a response with status 200 and JSON as type
          return Response.status(200).entity("User deleted").build();
      } else
          return Response.status(400).entity("Could not delete user").build();
  }

  // TODO: Make the system able to update users - fixed
  @PUT
  @Path("/{userId}/{token}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response updateUser(@PathParam("token") String token, String body) {

      User user1 = new Gson().fromJson(body, User.class);

      Boolean opdated = UserController.updateUser(user1,token);

      if (opdated){
          return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity("User is updated").build();
      } else
          // Return a response with status 200 and JSON as type
          return Response.status(400).entity("Endpoint not implemented yet").build();
  }
}