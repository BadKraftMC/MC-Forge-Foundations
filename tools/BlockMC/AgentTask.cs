using System.Diagnostics;
using System.Text;
using System.Text.Json;
using System.Threading.Tasks.Dataflow;


#nullable disable
namespace BlockMC {

    internal class AgentTask {
        private const string COMMON = "src\\main\\resources\\assets";
        private const string BLOCKSTATES = "blockstates";
        private const string MODELS = "models";
        private const string BLOCK = "block";
        private const string ITEM = "item";
        private const string TEXTURES = "textures";

        private Project Project { get; }
        private string Assets { get; set; }
        private JsonSerializerOptions JsonOptions { get; } = new JsonSerializerOptions() { WriteIndented = true };

        public AgentTask(Project project) {
            Project = project;

            Configure();
        }

        internal void Start( ) {
            if(Project.model.type == "block") {
                Write(Path.Combine(Project.mod, BLOCKSTATES), new BlockState(Assets, Project));
                Write(Path.Combine(Project.mod, MODELS, BLOCK), new ModelBlock(Assets, Project));
            }
            Write(Path.Combine(Project.mod, MODELS, ITEM), new ModelItem(Assets, Project));
            CopyTexture(Path.Combine(Project.mod, TEXTURES), Project.model);
        }

        private void Configure( ) {
            Debug.Assert(Directory.Exists(Project.root));

            Assets = Path.Combine(Project.root, COMMON);
        }
        private void CopyTexture(string destination, Model model) {
            string path = Path.Combine(Assets, destination, model.type);

            File.Copy($".\\data\\{Project.texture}", $"{path}\\{Project.texture}");
        }
        private void Write(string destination, StateTask stateTask) {
            string path = Path.Combine(Assets, destination, $"{stateTask.Name}.json");
            byte[] buffer = Encoding.UTF8.GetBytes(stateTask.ToString());

            if(!File.Exists(path)) {
                using(_ = File.Create(path)) { }
            }
            using(var stream = File.Open(path, FileMode.OpenOrCreate, FileAccess.ReadWrite)) {
                if(stream.Length > 0) {
                    stream.SetLength(0);
                }

                stream.Write(buffer);
            }
        }
    }
}
