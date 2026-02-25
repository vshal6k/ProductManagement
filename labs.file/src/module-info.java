/**
 * @author vishalkushwaha
 **/module labs.file {
     requires java.logging;
     requires labs.pm;
     provides labs.pm.service.ProductManager
             with labs.service.ProductFileManager;
}