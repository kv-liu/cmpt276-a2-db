/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

// import jdk.javadoc.internal.doclets.formats.html.SourceToHTMLConverter;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import javax.sql.DataSource;

import java.io.Console;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

@Controller
@SpringBootApplication
public class Main {

  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Autowired
  private DataSource dataSource;

  public static void main(String[] args) throws Exception {
    SpringApplication.run(Main.class, args);
  }

  @RequestMapping("/") // "webpage/" -> index.html.. listens for path "/" and executes String index function which redirects to index.html
  String index(Map<String, Object> model) {
    return "index";
  }

  @GetMapping( //when server hears get request from /input path ->redirects to the form at /input path
    path = "/input"
  )
  public String getInputForm(Map<String, Object> model) {
    Input input = new Input(); //passed input into /input
    model.put("input", input);
    return "input";
  }




  @PostMapping(  //when server hears post request from /input path
    path = "/input",
    consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
  )
  public String handleInputSubmit(Map<String, Object> model, Input input) throws Exception {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS rectangle (id serial, name varchar(30), width int, height int, color varchar(30)");
      String sql = "INSERT INTO rectangle (name, width, height, color) VALUES ('" + input.getName() + "','" + input.getWidth() + "' ,'" + input.getHeight() + "','" + input.getColor() + "')";
      ResultSet rs = stmt.executeQuery("SELECT * FROM rectangle");
      ArrayList<String> output = new ArrayList<String>();

      while (rs.next()) {
        output.add(rs.getString("name"));
      }
      if (!output.contains(input.getName())) {
        stmt.executeUpdate(sql);
      } else {
        System.out.println("Input NOT ADDED...");
        return "error";
      }
    
      System.out.println("Input: " + input);
      System.out.println(input.getName() + " " + input.getWidth() + " " + input.getHeight() + " " + input.getColor());
      System.out.println("postM input Model: " + model);

      return "redirect:/input/success";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @GetMapping("/input/success")
  public String getInputSuccess() {
    return "success";
  }



  @GetMapping(
    path = "/db/info/delete"
  )
  public String handleInputDel(Map<String, Object> model, @RequestParam String name) throws Exception {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS rectangle (id serial, name varchar(20), width varint(20), height varint(20), color varchar(20))");
      String sql = "DELETE FROM rectangle " + "WHERE (name='" + name + "')";
      stmt.executeUpdate(sql);
      System.out.println("(DELETION) GET Input Name: " + name);
      return "redirect:/db/info/deleted";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @GetMapping("/db/info/deleted")
  public String getInputSuccessDel() {
    return "delete";
  }

  @GetMapping(
    path= "db/info/update"
  )
  public String handleUpdateDirect(Map<String, Object> model, @RequestParam String name) throws Exception {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM rectangle " + "WHERE (name='"+name+"')");
      Input output = new Input();

      while (!rs.next()) {
        rs.getString("color");
        rs.getInt("Width");
        rs.getInt("Height");
      }
      output.setName(rs.getString("name"));

      model.put("inputObjUpdate", output);

      System.out.println("WE NEED THIS VAL:" + name);
      return "update";

    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  //this goes to update
  // @GetMapping (
  //   path ="db/info/update"
  // )
  // public String getUpdateForm(Map<String, Object> model) {
  //   Input input = new Input(); //passed input into /input
  //   model.put("input", input);
  //   return "update";
  // }

  


  @PostMapping(
    path = "/update"
  )
  public String handleInputUpdate(Map<String, Object> model, @RequestParam String name, Input input) throws Exception {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      System.out.println("Made it to POSTMAPPING /UPDATE");
      System.out.println(input.getName() + " " + input.getWidth() + " " + input.getHeight() + " " + input.getColor());
      System.out.println(name);
      System.out.println("UPDATE rectangle SET name ='" + input.getName() + "', width ='"+ input.getWidth() +"', height ='"+ input.getHeight() +"', color ='"+ input.getColor() +"' WHERE (name='" + name + "')");
      String sql = "UPDATE rectangle SET width ='"+ input.getWidth() +"', height ='"+ input.getHeight() +"', color ='"+ input.getColor() +"' WHERE (name='" + name + "')";
      // name ='" + input.getName() + "', 
      stmt.executeUpdate(sql);
      System.out.println("made to end?");
      return "redirect:/update/success";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @GetMapping("/update/success")
  public String getUpdatesuccess() {
    return "updateSuccess";
  }
  


  //new
  @GetMapping("db/info")
  public String getInputName(Map<String, Object> model, @RequestParam String name) {
    try (Connection connection = dataSource.getConnection()) {
    Statement stmt = connection.createStatement();
    System.out.println("GET Input Name: " + name);
    ResultSet rs = stmt.executeQuery("SELECT * FROM rectangle " + "WHERE (name='"+name+"')");
    Input output = new Input();

    while (!rs.next()) {
      rs.getString("color");
      rs.getInt("Width");
      rs.getInt("Height");
    }
    output.setName(rs.getString("name"));
    System.out.println("GET. color : " + rs.getString("color"));
    System.out.println("GET. width : " + rs.getInt("Width"));
    System.out.println("GET. height : " + rs.getInt("Height"));

    System.out.println("GET. Obj NAME : " + output.getName());
    
    model.put("inputName", name);
    model.put("inputColor", rs.getString("color"));
    model.put("inputWidth", rs.getInt("Width"));
    model.put("inputHeight", rs.getInt("Height"));
    model.put("inputObj", output);

    return "info";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }



  @RequestMapping("/db")
  String db(Map<String, Object> model) {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)");
      stmt.executeUpdate("INSERT INTO ticks VALUES (now())");
      ResultSet rs = stmt.executeQuery("SELECT * FROM rectangle");
      ArrayList<Input> output = new ArrayList<Input>();
      
      while (rs.next()) {
        Input temp = new Input();
        temp.setName(rs.getString("name"));
        temp.setColor(rs.getString("color"));
        output.add(temp);
        System.out.println("Result set name: " + rs.getString("name"));
        System.out.println("Result set color: " + rs.getString("color"));
      }
      model.put("inputs", output);
      return "db";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }



  @Bean
  public DataSource dataSource() throws SQLException {
    if (dbUrl == null || dbUrl.isEmpty()) {
      return new HikariDataSource();
    } else {
      HikariConfig config = new HikariConfig();
      config.setJdbcUrl(dbUrl);
      return new HikariDataSource(config);
    }
  }

}
