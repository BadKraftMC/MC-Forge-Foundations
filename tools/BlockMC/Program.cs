// See https://aka.ms/new-console-template for more information
using System.Text.Json;
using System.Xml.Schema;

using BlockMC;


#nullable disable


Project project = null;

//CreateFile(".\\data\\project.json");
Load(".\\data\\project.json");

RunTask(project);

Console.WriteLine("Hit Enter to Exit...");
Console.ReadKey();

void RunTask(Project project) {
    AgentTask agent = new(project);
    agent.Start();
}

void CreateFile(string fileName) {
    if(!Directory.Exists("data")) {
        Directory.CreateDirectory("data");
    }

    project = new() {
        root = "..\\..\\..\\..\\..\\1.18.2\\Foundations",
        mod = "foundations",
        model = new Model() {
            type = "block",
            item_parent = "clay_block",
            block_parent = "cube_all"
        },
        name = "foo",
        texture = "foo.png"
    };

    if(!File.Exists(fileName)) {
        using(var file = File.Create(fileName)) { }
    }
    else {
        using(var file = File.OpenWrite(fileName)) {
            file.SetLength(0);
        }
    }

    using(var stream = File.OpenWrite(fileName)) {
        JsonSerializer.Serialize(stream, project, typeof(Project), new JsonSerializerOptions() { WriteIndented = true });
    }
}

void Load(string fileName) {
    if(File.Exists(fileName)) {
        using(var stream = File.OpenRead(fileName)) {
            project = JsonSerializer.Deserialize<Project>(stream);
        }
    }
}